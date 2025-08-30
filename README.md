# Spring Boot Thread & Connection Pool 관리 완전 가이드

## 🎯 개요

Spring Boot 애플리케이션에서 HTTP 요청 처리 시 발생하는 다양한 스레드와 Connection Pool을 효율적으로 관리하기 위한 종합 가이드입니다.

---

## 🏗️ 스레드 아키텍처 구조

```
HTTP 요청 → [Tomcat NIO] → [Tomcat Thread Pool] → [Spring Controller]
                                    ↓
                            [비즈니스 로직 실행]
                                    ↓
                    ┌─────────────────────────────────┐
                    │                                 │
          [DB 작업] ←→ [HikariCP Connection Pool]     │
                    │                                 │
          [비동기 작업] → [Custom Thread Pools]       │
                    │   - DB Pool                     │
                    │   - API Pool                    │
                    │   - Compute Pool                │
                    │   - Notification Pool           │
                    └─────────────────────────────────┘
```

---

## 📊 주요 Thread Pool 및 Connection Pool

### 1. Tomcat Thread Pool
- **역할**: HTTP 요청 처리 담당
- **생명주기**: 요청 수신 → 비즈니스 로직 실행 → 응답 전송
- **기본값**: min=10, max=200, queue=100

### 2. HikariCP Connection Pool
- **역할**: 데이터베이스 연결 관리
- **특징**: Connection 재사용으로 성능 최적화
- **기본값**: pool-size=10 (운영환경에서는 부족)

### 3. Custom Thread Pools
- **역할**: 특정 작업 유형별 전담 처리
- **장점**: 장애 격리, 세밀한 튜닝, 예측 가능한 리소스 사용

---

## ⚙️ 권장 설정

### 기본 설정 (application.yml)
```yaml
# Tomcat 설정
server:
  tomcat:
    threads:
      min-spare: 20          # 최소 유지 스레드
      max: 200              # 최대 스레드 
    accept-count: 100        # 대기 큐 크기
    max-connections: 8192    # 최대 동시 연결

# HikariCP 설정
spring:
  datasource:
    hikari:
      minimum-idle: 20           # 최소 유지 커넥션
      maximum-pool-size: 100     # 최대 커넥션 (Tomcat의 50%)
      max-lifetime: 1800000      # 커넥션 수명 (30분)
      idle-timeout: 600000       # 유휴 타임아웃 (10분)
      connection-timeout: 3000   # 커넥션 대기 시간 (3초)
      leak-detection-threshold: 60000  # 누수 감지 (1분)
```

### 역할별 Thread Pool 설정
```java
@Configuration
public class ThreadPoolConfig {
    
    @Bean("databasePool")
    public Executor databasePool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("db-");
        return executor;
    }
    
    @Bean("externalApiPool")
    public Executor externalApiPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(60);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("api-");
        return executor;
    }
    
    @Bean("computePool")
    public Executor computePool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("cpu-");
        return executor;
    }
}
```

---

## 🚨 주요 위험 상황 및 대응

### 1. 스레드 폭발 (Thread Explosion)
**문제**: `CompletableFuture.supplyAsync()` 무분별 사용
```java
// ❌ 위험한 코드
CompletableFuture.supplyAsync(() -> dbWork());  // 스레드 무한 생성 가능

// ✅ 안전한 코드  
CompletableFuture.supplyAsync(() -> dbWork(), databasePool);  // 제한된 풀 사용
```

### 2. Connection Pool 부족
**문제**: Tomcat Thread >> HikariCP Connection
**해결**: 비율 조정 (일반적으로 2:1 ~ 4:1)

### 3. CPU 사용률 관리
- **평상시**: 50-70%
- **긴급시**: 최대 80%
- **위험**: 90% 이상 (즉시 대응 필요)

---

## 📈 모니터링 필수 지표

### 실시간 모니터링 코드
```java
@Component
public class PoolMonitor {
    
    @Scheduled(fixedRate = 10000)
    public void monitorPools() {
        // Tomcat 모니터링
        logTomcatStats();
        
        // HikariCP 모니터링  
        logHikariStats();
        
        // 전체 JVM 스레드 모니터링
        logJvmThreadStats();
    }
    
    private void logTomcatStats() {
        // tomcat.threads.busy, tomcat.threads.current, tomcat.threads.config.max
    }
    
    private void logHikariStats() {
        // hikaricp.connections.active, hikaricp.connections.idle
        // hikaricp.connections.pending (대기 중인 스레드 수)
    }
}
```

### 핵심 메트릭
1. **tomcat.threads.busy** - 현재 바쁜 스레드 수
2. **tomcat.threads.config.max** - 최대 스레드 수
3. **hikaricp.connections.active** - 활성 DB 커넥션 수
4. **hikaricp.connections.pending** - DB 커넥션 대기 스레드 수
5. **jvm.threads.live** - 전체 JVM 스레드 수

### 알람 기준
```java
// 1차 알람 (주의)
if (tomcatThreadUsage > 70%) alert("Tomcat thread usage high");
if (hikariConnectionUsage > 80%) alert("DB connection usage high");

// 2차 알람 (위험)
if (hikariPendingConnections > 10) alert("DB connection bottleneck");
if (cpuUsage > 85%) alert("CPU usage critical");

// 3차 알람 (긴급)
if (jvmThreadCount > 500) alert("Thread explosion detected");
```

---

## 🔧 Runtime 동적 조정

### Tomcat Thread Pool 동적 변경
```java
@RestController
public class PoolManagementController {
    
    @PostMapping("/admin/scale-tomcat")
    public ResponseEntity<String> scaleTomcatThreads(@RequestParam int newMaxThreads) {
        AbstractHttp11Protocol<?> protocol = getProtocol();
        protocol.setMaxThreads(newMaxThreads);
        return ResponseEntity.ok("Tomcat threads scaled to: " + newMaxThreads);
    }
}
```

### 자동 스케일링
```java
@Component 
public class AutoScaler {
    
    @Scheduled(fixedRate = 30000)
    public void autoScale() {
        double usage = getCurrentUsage();
        
        if (usage > 0.8) {
            scaleUp();
        } else if (usage < 0.3) {
            scaleDown();
        }
    }
}
```

---

## 🎯 서비스 타입별 권장 설정

### E-Commerce (DB 집약적)
```yaml
server.tomcat.threads.max: 200
spring.datasource.hikari.maximum-pool-size: 100  # 1:2 비율
```

### API Gateway (I/O 집약적)
```yaml
server.tomcat.threads.max: 500
spring.datasource.hikari.maximum-pool-size: 50   # 1:10 비율
```

### 데이터 처리 서버 (CPU 집약적)
```yaml  
server.tomcat.threads.max: 100
spring.datasource.hikari.maximum-pool-size: 20   # 1:5 비율
```

---

## ⚠️ 주의사항 및 Best Practices

### DO
- ✅ 역할별로 전용 Thread Pool 생성
- ✅ Connection Pool 크기를 Tomcat Thread의 50-70%로 설정
- ✅ 실시간 모니터링 및 알람 설정
- ✅ 부하 테스트로 최적값 찾기
- ✅ Runtime 동적 조정 기능 구현

### DON'T
- ❌ CompletableFuture 기본 풀 사용 (ForkJoinPool.commonPool)
- ❌ Spring Boot 기본값 그대로 운영 환경 사용
- ❌ Connection Pool만 늘리고 Tomcat Thread는 그대로 두기
- ❌ 모니터링 없이 설정값만 조정
- ❌ CPU 사용률 95% 이상 장기간 유지

---

## 📋 체크리스트

### 설정 전 확인사항
- [ ] 현재 애플리케이션의 요청 패턴 분석
- [ ] DB 사용 비율 측정 (전체 요청 중 몇 %가 DB 사용?)
- [ ] 외부 API 호출 패턴 분석
- [ ] CPU vs I/O 집약적 작업 비율 확인

### 설정 후 검증사항
- [ ] 부하 테스트 실행
- [ ] 메모리 사용량 모니터링
- [ ] 응답 시간 개선 확인
- [ ] 에러율 모니터링
- [ ] 알람 정상 동작 확인

---

## 🚀 결론

효율적인 Spring Boot 애플리케이션을 위해서는:

1. **각 역할별 전담 Thread Pool** 설계
2. **적절한 Connection Pool 비율** 유지
3. **실시간 모니터링** 시스템 구축
4. **동적 스케일링** 기능 구현
5. **지속적인 성능 튜닝** 수행

이를 통해 안정적이고 확장 가능한 멀티스레드 애플리케이션을 구축할 수 있습니다.

---

## 🌐 MSA 통신 및 병렬 처리 최적화

### MSA 환경에서의 비동기 처리 패턴

#### 1. 순차 처리 vs 병렬 처리 비교

```java
// ❌ 순차 처리 (비효율적)
@GetMapping("/user/{id}/dashboard-sync")
public ResponseEntity<UserDashboard> getDashboardSync(@PathVariable Long id) {
    // 총 소요시간: 2000ms (각각 500ms씩)
    User user = userServiceClient.getUser(id);              // 500ms
    List<Order> orders = orderServiceClient.getOrders(id);  // 500ms  
    Profile profile = profileServiceClient.getProfile(id);  // 500ms
    List<Product> recommendations = recommendServiceClient.getRecommendations(id); // 500ms
    
    return ResponseEntity.ok(buildDashboard(user, orders, profile, recommendations));
}

// ✅ 병렬 처리 (효율적)
@GetMapping("/user/{id}/dashboard-async")
public CompletableFuture<ResponseEntity<UserDashboard>> getDashboardAsync(@PathVariable Long id) {
    // 총 소요시간: 500ms (병렬 실행)
    CompletableFuture<User> userFuture = CompletableFuture
        .supplyAsync(() -> userServiceClient.getUser(id), externalApiPool);
        
    CompletableFuture<List<Order>> ordersFuture = CompletableFuture
        .supplyAsync(() -> orderServiceClient.getOrders(id), externalApiPool);
        
    CompletableFuture<Profile> profileFuture = CompletableFuture
        .supplyAsync(() -> profileServiceClient.getProfile(id), externalApiPool);
        
    CompletableFuture<List<Product>> recommendationsFuture = CompletableFuture
        .supplyAsync(() -> recommendServiceClient.getRecommendations(id), externalApiPool);
    
    return CompletableFuture.allOf(userFuture, ordersFuture, profileFuture, recommendationsFuture)
        .thenApply(v -> ResponseEntity.ok(buildDashboard(
            userFuture.join(),
            ordersFuture.join(), 
            profileFuture.join(),
            recommendationsFuture.join()
        )));
}
```

#### 2. @Async vs CompletableFuture 선택 기준

```java
// @Async - 단순한 비동기 작업, 결과 조합 불필요
@Service
public class NotificationService {
    
    @Async("notificationPool")
    public void sendWelcomeEmail(Long userId) {
        // Fire-and-forget 패턴
        // 결과값이 필요없는 단순 비동기 작업
        emailService.sendWelcomeEmail(userId);
    }
    
    @Async("notificationPool")
    public CompletableFuture<Boolean> sendOrderConfirmation(Long orderId) {
        // 결과값이 필요한 경우 CompletableFuture 반환
        boolean result = emailService.sendOrderConfirmation(orderId);
        return CompletableFuture.completedFuture(result);
    }
}

// CompletableFuture - 복잡한 결과 조합, 체이닝 필요
@Service  
public class MSAIntegrationService {
    
    public CompletableFuture<CompleteOrderInfo> getCompleteOrderInfo(Long orderId) {
        // 복잡한 결과 조합이 필요한 경우
        CompletableFuture<Order> orderFuture = CompletableFuture
            .supplyAsync(() -> orderService.getOrder(orderId), externalApiPool);
            
        CompletableFuture<User> userFuture = orderFuture
            .thenComposeAsync(order -> CompletableFuture
                .supplyAsync(() -> userService.getUser(order.getUserId()), externalApiPool));
                
        CompletableFuture<List<OrderItem>> itemsFuture = orderFuture
            .thenComposeAsync(order -> CompletableFuture
                .supplyAsync(() -> productService.getOrderItems(order.getItemIds()), externalApiPool));
        
        return CompletableFuture.allOf(orderFuture, userFuture, itemsFuture)
            .thenApply(v -> CompleteOrderInfo.builder()
                .order(orderFuture.join())
                .user(userFuture.join())
                .items(itemsFuture.join())
                .build());
    }
}
```

#### 3. CPU 자원 활용 최적화 전략

```java
@Service
public class OptimizedProcessingService {
    
    // CPU 집약적 작업을 위한 전용 스레드풀
    @Bean("cpuIntensivePool")  
    public Executor cpuIntensivePool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2); 
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("cpu-intensive-");
        return executor;
    }
    
    // I/O 대기 중 CPU 자원 활용
    public CompletableFuture<ProcessedResult> processWithCpuOptimization(Long dataId) {
        
        // 1. I/O 작업 시작 (외부 API/DB 호출)
        CompletableFuture<RawData> dataFuture = CompletableFuture
            .supplyAsync(() -> {
                log("데이터 로딩 시작 - 스레드: {}", Thread.currentThread().getName());
                return externalDataService.loadData(dataId); // I/O 블로킹 작업
            }, externalApiPool);
            
        // 2. I/O 대기 중에 CPU 집약적 전처리 작업 병렬 실행
        CompletableFuture<PreprocessConfig> configFuture = CompletableFuture
            .supplyAsync(() -> {
                log("설정 계산 시작 - 스레드: {}", Thread.currentThread().getName()); 
                return calculatePreprocessingConfig(dataId); // CPU 집약적 작업
            }, cpuIntensivePool);
            
        // 3. 두 작업 완료 후 최종 처리 (CPU 집약적)
        return CompletableFuture.allOf(dataFuture, configFuture)
            .thenComposeAsync(v -> CompletableFuture.supplyAsync(() -> {
                log("최종 처리 시작 - 스레드: {}", Thread.currentThread().getName());
                
                RawData data = dataFuture.join();
                PreprocessConfig config = configFuture.join();
                
                // CPU 집약적인 데이터 가공 작업
                return processDataIntensively(data, config);
            }, cpuIntensivePool));
    }
    
    // 메모리 집약적 작업 최적화
    public CompletableFuture<List<ProcessedItem>> processLargeDataset(List<Long> dataIds) {
        
        // 메모리 사용량 고려하여 배치 단위로 분할 처리
        int batchSize = 100; // 메모리에 따라 조정
        List<List<Long>> batches = partitionList(dataIds, batchSize);
        
        List<CompletableFuture<List<ProcessedItem>>> batchFutures = batches.stream()
            .map(batch -> CompletableFuture.supplyAsync(() -> {
                log("배치 처리 시작 - 크기: {}, 스레드: {}", 
                    batch.size(), Thread.currentThread().getName());
                    
                // 메모리 집약적 작업
                return processBatch(batch);
            }, cpuIntensivePool))
            .collect(Collectors.toList());
            
        // 모든 배치 완료 후 결과 합치기
        return CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0]))
            .thenApply(v -> batchFutures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList()));
    }
}
```

#### 4. 실무 MSA 통신 패턴

```java
@RestController
public class MSAOptimizedController {
    
    @Autowired @Qualifier("externalApiPool")
    private Executor externalApiPool;
    
    @Autowired @Qualifier("cpuIntensivePool")  
    private Executor cpuPool;
    
    // 패턴 1: 빠른 응답 + 백그라운드 처리
    @PostMapping("/orders")
    public ResponseEntity<OrderCreatedResponse> createOrder(@RequestBody CreateOrderRequest request) {
        
        // 1. 빠른 주문 생성 (필수 검증만)
        Order order = orderService.createOrderQuickly(request);
        
        // 2. 무거운 후처리 작업들을 백그라운드에서 비동기 실행  
        CompletableFuture.runAsync(() -> {
            // 재고 업데이트, 포인트 적립, 알림 발송 등
            inventoryService.updateStock(order.getItems());
            pointService.earnPoints(order.getUserId(), order.getAmount());
            notificationService.sendOrderConfirmation(order.getId());
        }, externalApiPool);
        
        // 3. 즉시 응답 (사용자는 빠른 피드백 받음)
        return ResponseEntity.ok(OrderCreatedResponse.builder()
            .orderId(order.getId())
            .status("CREATED")
            .message("주문이 접수되었습니다")
            .build());
    }
    
    // 패턴 2: 조건부 병렬 처리
    @GetMapping("/products/{id}/details")
    public CompletableFuture<ResponseEntity<ProductDetailResponse>> getProductDetails(
            @PathVariable Long id, 
            @RequestParam(defaultValue = "false") boolean includeRecommendations,
            @RequestParam(defaultValue = "false") boolean includeReviews) {
        
        // 필수 데이터
        CompletableFuture<Product> productFuture = CompletableFuture
            .supplyAsync(() -> productService.getProduct(id), externalApiPool);
            
        // 선택적 데이터 (조건에 따라 병렬 실행)
        CompletableFuture<List<Product>> recommendationsFuture = includeRecommendations 
            ? CompletableFuture.supplyAsync(() -> recommendationService.getRecommendations(id), externalApiPool)
            : CompletableFuture.completedFuture(Collections.emptyList());
            
        CompletableFuture<List<Review>> reviewsFuture = includeReviews
            ? CompletableFuture.supplyAsync(() -> reviewService.getReviews(id), externalApiPool) 
            : CompletableFuture.completedFuture(Collections.emptyList());
        
        return CompletableFuture.allOf(productFuture, recommendationsFuture, reviewsFuture)
            .thenApply(v -> {
                ProductDetailResponse response = ProductDetailResponse.builder()
                    .product(productFuture.join())
                    .recommendations(recommendationsFuture.join())
                    .reviews(reviewsFuture.join())
                    .build();
                    
                return ResponseEntity.ok(response);
            });
    }
    
    // 패턴 3: 장애 허용(Fault Tolerant) 병렬 처리
    @GetMapping("/user/{id}/dashboard-resilient")
    public CompletableFuture<ResponseEntity<UserDashboardResponse>> getResilientDashboard(@PathVariable Long id) {
        
        // 각 서비스 호출을 독립적으로 처리 (하나 실패해도 다른 것들은 정상 처리)
        CompletableFuture<Optional<User>> userFuture = CompletableFuture
            .supplyAsync(() -> userService.getUser(id), externalApiPool)
            .handle((result, ex) -> {
                if (ex != null) {
                    log.warn("User service 호출 실패: {}", ex.getMessage());
                    return Optional.<User>empty();
                }
                return Optional.of(result);
            });
            
        CompletableFuture<Optional<List<Order>>> ordersFuture = CompletableFuture
            .supplyAsync(() -> orderService.getUserOrders(id), externalApiPool)
            .handle((result, ex) -> {
                if (ex != null) {
                    log.warn("Order service 호출 실패: {}", ex.getMessage());
                    return Optional.<List<Order>>empty();
                }
                return Optional.of(result);
            });
        
        return CompletableFuture.allOf(userFuture, ordersFuture)
            .thenApply(v -> {
                UserDashboardResponse response = UserDashboardResponse.builder()
                    .user(userFuture.join().orElse(null))
                    .orders(ordersFuture.join().orElse(Collections.emptyList()))
                    .isPartialData(!userFuture.join().isPresent() || !ordersFuture.join().isPresent())
                    .build();
                    
                return ResponseEntity.ok(response);
            });
    }
}
```

#### 5. 성능 최적화 모니터링

```java
@Component
public class MSAPerformanceMonitor {
    
    @EventListener
    public void onCompletableFutureComplete(CompletableFutureCompletedEvent event) {
        long executionTime = event.getExecutionTime();
        String threadName = event.getThreadName();
        
        // 비동기 작업 성능 트래킹
        meterRegistry.timer("async.execution.time", "thread.pool", getPoolName(threadName))
            .record(executionTime, TimeUnit.MILLISECONDS);
            
        if (executionTime > 5000) { // 5초 이상 걸린 작업 알람
            log.warn("장시간 실행된 비동기 작업 감지: {}ms, 스레드: {}", executionTime, threadName);
        }
    }
    
    @Scheduled(fixedRate = 30000)
    public void monitorMSACommunication() {
        // MSA 간 통신 성능 모니터링
        double avgResponseTime = getAverageResponseTime();
        int failureRate = getFailureRate();
        
        log.info("MSA 통신 성능 - 평균 응답시간: {}ms, 실패율: {}%", avgResponseTime, failureRate);
        
        if (avgResponseTime > 1000) {
            // 응답시간이 1초를 초과하면 스레드풀 확장 검토
            recommendThreadPoolScaling();
        }
    }
}
```

#### 6. Best Practices 요약

```java
// ✅ 올바른 패턴들

// 1. 역할별 스레드풀 분리
@Qualifier("externalApiPool")    // MSA 통신용
@Qualifier("cpuIntensivePool")   // CPU 집약적 작업용  
@Qualifier("notificationPool")   // 알림 발송용

// 2. 적절한 타임아웃 설정
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(this::callExternalAPI, externalApiPool)
    .orTimeout(3, TimeUnit.SECONDS);  // 3초 타임아웃

// 3. 예외 처리
CompletableFuture<String> resilientFuture = CompletableFuture
    .supplyAsync(this::riskyOperation, externalApiPool)
    .exceptionally(throwable -> {
        log.error("작업 실패, 기본값 반환", throwable);
        return "기본값";
    });

// 4. 리소스 정리
@PreDestroy
public void cleanup() {
    // 애플리케이션 종료시 스레드풀 정리
    ((ThreadPoolTaskExecutor) externalApiPool).shutdown();
}
```

### 핵심 원칙

1. **I/O 대기 중 CPU 자원 최대 활용**
2. **MSA 호출은 항상 병렬로 처리**
3. **장애 허용(Fault Tolerance) 설계**
4. **적절한 배치 크기로 메모리 최적화**
5. **실시간 성능 모니터링 및 알람**

---

*"스레드와 커넥션 풀 관리는 애플리케이션 성능의 핵심입니다. 모니터링하고, 측정하고, 최적화하세요."*