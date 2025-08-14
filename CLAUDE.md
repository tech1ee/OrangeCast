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

### Loading States and Animation Standards
- ✅ **Shimmer effects** for all loading states instead of circular progress indicators
- ✅ Shimmer components for cards, images, and text placeholders
- ✅ Consistent loading animation timing (1200ms duration with LinearEasing)
- ✅ Platform-agnostic shimmer implementation in shared/commonMain
- ✅ Real image loading with shimmer fallback (SubcomposeAsyncImage on Android, custom implementation on iOS)

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

## 📝 TASK MANAGEMENT INTEGRATION

### Task Status Tracking
- ✅ **Primary Source**: `TASKS.md` contains all active development tasks
- ✅ **Task Format**: TASK-XXX with detailed descriptions and acceptance criteria
- ✅ **Status Updates**: Must be reflected in TASKS.md after each work session
- ✅ **Dependencies**: Clear task dependency mapping to avoid conflicts

### Task Execution Rules
- ❌ **NO** starting new tasks without updating previous task status
- ✅ **ALWAYS** mark tasks as "IN_PROGRESS" before beginning work
- ✅ **IMMEDIATELY** update status to "COMPLETED" when finished  
- ✅ **Document** any blockers or issues encountered in task notes
- ✅ **Test** each task completion against its acceptance criteria

### Priority Enforcement
1. **CRITICAL**: Must be completed before any other work
2. **HIGH**: Complete after all critical tasks
3. **MEDIUM**: Background/parallel work when possible
4. **LOW**: Future iteration planning only

### Task Documentation Standards
- ✅ Each task must have clear technical solution steps
- ✅ All affected files must be explicitly listed
- ✅ Acceptance criteria must be testable and specific
- ✅ Time estimates should be realistic and trackable
- ✅ Dependencies must be clearly mapped

### Current Project State Integration
- ✅ **Active Task Source**: `/TASKS.md` 
- ✅ **Status Reports**: `/PROJECT_STATUS_REPORT.md`
- ✅ **Action Plans**: `/ACTION_PLAN.md`
- ✅ **Testing Results**: `/screenshots/` directory for UI validation

## 🧪 AUTOMATED TESTING RULES

### UI Testing Standards
- ✅ **Use UI Automator over ADB coordinates** - Element-based selectors are reliable
- ✅ **Use element properties** - resource-id, text, content-desc for stable tests
- ✅ **Generate UI hierarchy dumps** for test analysis and debugging
- ❌ **NO** hardcoded coordinates - they break across devices and updates
- ❌ **NO** pixel-perfect coordinate testing - use bounds-based center calculations

### Testing Workflow
1. **Build and Install**: `./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk`
2. **Launch App**: `adb shell am start -n com.orangecast.app.debug/com.orangecast.ui.MainActivity`
3. **Dump UI**: `adb shell uiautomator dump /sdcard/ui_dump.xml && adb pull /sdcard/ui_dump.xml`
4. **Analyze Elements**: Parse XML for bounds, text, resource-id properties
5. **Calculate Centers**: Use bounds `[left,top][right,bottom]` to get center coordinates
6. **Execute Tests**: `adb shell input tap X Y` with calculated centers
7. **Verify Results**: Take screenshots and validate UI state changes

### Element Selection Priority
1. **resource-id** (highest priority) - Most stable identifier
2. **text** (medium priority) - Works for buttons and labels  
3. **content-desc** (medium priority) - Accessibility descriptions
4. **bounds** (lowest priority) - Only for coordinate calculation

### Test Data Management
- ✅ **Real screenshots** in `/screenshots/` with descriptive names
- ✅ **UI dumps** in `/tmp/` for analysis (not committed to git)
- ✅ **Test results** documented with before/after evidence
- ❌ **NO** mock or synthetic test data in UI testing

### Performance Standards
- ✅ **Fast execution** - UI Automator tests should complete in <30 seconds
- ✅ **Reliable results** - 99%+ success rate on repeated runs
- ✅ **Clear output** - Screenshot evidence for every major test step
- ✅ **Efficient debugging** - UI dumps available for troubleshooting

---

**Remember: These rules are non-negotiable. Every line of code must comply with these standards to maintain the project's high quality and cross-platform consistency. TASKS.md is the single source of truth for development priorities.**