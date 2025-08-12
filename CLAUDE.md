# CLAUDE.md - Development Rules and Guidelines for OrangeCast

## 🎯 Core Principles

### 1. NO FAKE IMPLEMENTATIONS
- ❌ **NEVER** create mock, fake, or placeholder implementations
- ✅ **ALWAYS** connect to real APIs, real data, real functionality
- ✅ Every feature must work with actual podcast content
- ✅ All network calls must hit real endpoints (iTunes API, RSS feeds)
- ✅ Database operations must use real SQLite/SQLDelight
- ✅ Audio playback must use real ExoPlayer/AVPlayer with actual audio files

### 2. CODE SHARING REQUIREMENTS
- ✅ **90%+ shared code** between Android and iOS platforms
- ✅ Business logic, data models, use cases in `shared/src/commonMain/`
- ✅ Platform-specific code only in `androidMain/` and `iosMain/`
- ✅ UI components shared via Compose Multiplatform
- ✅ Repository pattern with platform-specific implementations
- ✅ Single source of truth for all business rules

### 3. NO DEBUG ARTIFACTS IN PRODUCTION
- ❌ **NO** `println()`, `console.log()`, or debug print statements
- ❌ **NO** hardcoded test data or development shortcuts  
- ❌ **NO** `// TODO:` comments left in production code
- ✅ Use proper logging framework (Napier) for production logging
- ✅ Remove all development artifacts before committing
- ✅ Clean, production-ready code only

### 4. SELF-DOCUMENTING CODE
- ❌ **NO** explanatory comments in code
- ✅ Code must be self-explanatory through naming
- ✅ Clear class names, function names, variable names
- ✅ Well-structured code that tells its own story
- ✅ Use meaningful domain language in naming
- ✅ Prefer longer descriptive names over comments

### 5. DRY PRINCIPLE (Don't Repeat Yourself)
- ❌ **NO** duplicate business logic across platforms
- ❌ **NO** copy-pasted code blocks
- ✅ Extract common functionality to shared modules
- ✅ Single implementation of business rules
- ✅ Platform-specific adapters only when necessary
- ✅ Reusable components and utilities

## 🏗️ Architecture Rules

### Modular Architecture
```
shared/
├── src/commonMain/           # 90% of application logic
├── src/androidMain/          # Android-specific implementations
└── src/iosMain/              # iOS-specific implementations

modules/
├── player/                   # Audio player functionality
├── podcasts/                 # Podcast discovery and management
├── library/                  # User library and subscriptions
└── details/                  # Podcast details and episodes
```

### Layer Separation
- **Data Layer**: Repositories, APIs, database, caching
- **Domain Layer**: Use cases, business logic, domain models
- **Presentation Layer**: ViewModels, UI state, user interactions
- **UI Layer**: Compose screens, platform-specific UI adaptations

### Dependency Injection
- Use Koin for dependency injection in shared code
- Platform-specific DI only where absolutely necessary
- Clear separation between interface and implementation

## 🔐 Security Requirements

### API Security
- ✅ **Environment-based API key management** (no hardcoded keys)
- ✅ Certificate pinning for network requests
- ✅ Input validation and sanitization
- ✅ Secure storage for sensitive data
- ❌ **NO** API keys in source code or version control

### Data Protection
- ✅ Encrypt user preferences and sensitive data
- ✅ Secure database transactions
- ✅ Handle network failures gracefully
- ✅ Validate all external data sources

## 📊 Performance Standards

### Memory Management
- ✅ Maximum 100MB memory usage during normal operation
- ✅ Proper cleanup of resources (databases, network, audio)
- ✅ Efficient image loading and caching
- ✅ Prevent memory leaks in long-running services

### Network Optimization
- ✅ Request deduplication and intelligent caching
- ✅ Batch API calls where possible
- ✅ Offline-first architecture with sync
- ✅ Efficient RSS feed parsing and storage

### Battery Efficiency
- ✅ Minimal background processing
- ✅ Efficient audio codec usage
- ✅ Smart sync strategies
- ✅ CPU-optimized algorithms

## 🧪 Quality Assurance

### Testing Requirements
- ✅ Unit tests for all use cases and repositories
- ✅ Integration tests for API interactions
- ✅ Platform-specific testing for audio playback
- ✅ Performance testing under various conditions
- ❌ **NO** tests with mocked business logic

### Code Quality
- ✅ Kotlin coding conventions
- ✅ Consistent formatting and style
- ✅ Error handling for all external dependencies
- ✅ Proper resource management and cleanup
- ✅ Thread safety for shared state

## 🎨 UI/UX Standards

### Cross-Platform Consistency
- ✅ 95%+ identical user experience on both platforms
- ✅ Shared UI components via Compose Multiplatform
- ✅ Platform-specific adaptations only for native feel
- ✅ Consistent navigation patterns
- ✅ Unified design system and theming

### Accessibility
- ✅ Screen reader support for all interactive elements
- ✅ Proper semantic descriptions
- ✅ Keyboard navigation support
- ✅ High contrast mode compatibility
- ✅ Text scaling support

## 🚀 Deployment Standards

### Build Configuration
- ✅ Separate configurations for debug/release
- ✅ ProGuard/R8 optimization for Android
- ✅ iOS app store optimization
- ✅ Automated testing in CI/CD pipeline
- ✅ Version consistency across platforms

### Release Process
- ✅ Thorough QA testing before release
- ✅ Performance benchmarking
- ✅ Security audit of new features
- ✅ User acceptance testing
- ✅ Rollback plan for critical issues

## ⚠️ CRITICAL REMINDERS

### Before Every Commit
1. **Verify NO fake implementations** - everything must work with real data
2. **Check code sharing percentage** - maintain 90%+ shared logic
3. **Remove all debug artifacts** - no print statements or TODOs
4. **Ensure self-documenting code** - no explanatory comments needed
5. **Validate DRY principle** - no duplicate logic across platforms

### Development Workflow
1. **Start with shared business logic** in `commonMain`
2. **Add platform-specific implementations** only when required
3. **Test with real data** from actual APIs and RSS feeds
4. **Validate cross-platform consistency** on both Android and iOS
5. **Performance test** under various network and device conditions

### Quality Gates
- ❌ **Cannot merge** code with fake implementations
- ❌ **Cannot merge** code below 90% sharing threshold
- ❌ **Cannot merge** code with debug artifacts
- ❌ **Cannot merge** code with explanatory comments
- ❌ **Cannot merge** code with duplicate business logic

## 📋 Compliance Checklist

After implementing any feature, verify:

- [ ] **Real Implementation**: Feature works with actual data/APIs
- [ ] **Code Sharing**: 90%+ business logic in shared module
- [ ] **Clean Code**: No debug prints, TODOs, or development artifacts
- [ ] **Self-Documenting**: Code explains itself without comments
- [ ] **DRY Compliance**: No duplicate logic across platforms
- [ ] **Performance**: Meets memory and battery requirements
- [ ] **Security**: No hardcoded secrets or vulnerabilities
- [ ] **Testing**: Comprehensive test coverage with real scenarios
- [ ] **Cross-Platform**: Identical behavior on Android and iOS
- [ ] **Architecture**: Follows established patterns and layer separation

## 🎯 SUCCESS METRICS

### Technical Metrics
- **Code Sharing**: >90% business logic shared
- **Performance**: <100MB memory, <3s startup, <5% battery/hour
- **Quality**: 0 critical bugs, >95% test coverage
- **Security**: 0 hardcoded secrets, encrypted sensitive data

### User Experience Metrics  
- **Cross-Platform Consistency**: >95% identical experience
- **Reliability**: >99.5% uptime, <1% crash rate
- **Accessibility**: WCAG 2.1 AA compliance
- **Performance**: <200ms response times, smooth 60fps UI

---

**Remember: These rules are non-negotiable. Every line of code must comply with these standards to maintain the project's high quality and cross-platform consistency.**