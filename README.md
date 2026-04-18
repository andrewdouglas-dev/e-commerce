# e-commerce

**My most advanced Java backend project to date.**  
A full-featured e-commerce system that demonstrates production-ready architecture patterns, building directly on concepts from my [Weather Data Proxy API](https://github.com/andrewdouglas-dev/weatherapi) project.

## ✨ Key Features & Learning Outcomes

- **Advanced Redis caching with Token Bucket rate limiting**  
  More efficient and fair than fixed-window algorithms. Prevents abuse while maximizing throughput.

- **Lazy loading patterns**  
  Optimized data fetching to reduce initial load times and database pressure.

- **Graceful degradation**  
  The application continues to serve requests even when Redis or the database is temporarily unavailable.

- **Full multi-container Docker setup**  
  Production-like environment with Docker Compose (Spring-like Java app + Redis + MySQL 8). Includes health checks, persistent volumes, and initialization scripts.

- **Secure authentication**  
  Argon2 password hashing + environment-driven configuration via dotenv.

- **Realistic test data**  
  Generated with Datafaker for development and testing.

## 🛠 Tech Stack

- **Language**: Java 17
- **Build**: Maven
- **Database**: MySQL 8.0
- **Cache / Rate Limiting**: Redis (Jedis client) + custom Token Bucket
- **Security**: Argon2-jvm
- **Other**: Gson, Datafaker, dotenv-java
- **Containerization**: Docker + Docker Compose
- **Packaging**: Maven Shade Plugin (executable JAR)
