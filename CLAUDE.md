# CLAUDE.md - Development Rules and Guidelines for OrangeCast

## 🎯 Core Principles

### 1. NO FAKE IMPLEMENTATIONS
- ❌ **NEVER** create mock, fake, or placeholder implementations
- ✅ **ALWAYS** connect to real APIs, real data, real functionality
- ✅ Every feature must work with actual podcast content
- ✅ All network calls must hit real endpoints (iTunes API, RSS feeds, PodcastIndex)
- ✅ Database operations must use real SQLite/SQLDelight
- ✅ Audio playback must use real ExoPlayer/AVPlayer with actual audio files
- ✅ Google Drive sync must use real Google Auth and Drive API

### 2. CLEAN CODE ARCHITECTURE
- ✅ **SOLID Principles** - Single responsibility, Open/closed, Liskov substitution, Interface segregation, Dependency inversion
- ✅ **Clean Architecture** - Clear layer separation (UI, Presentation, Domain, Data)
- ✅ **Clean Code Practices** - Meaningful names, small functions, no side effects
- ❌ **NO** overcomplicated abstractions or unnecessary complexity
- ❌ **NO** premature optimization or over-engineering
- ✅ Simple, readable, maintainable code that follows best practices

### 3. CODE SHARING REQUIREMENTS
- ✅ **90%+ shared code** between Android and iOS platforms
- ✅ Business logic, data models, use cases in `shared/src/commonMain/`
- ✅ Platform-specific code only in `androidMain/` and `iosMain/`
- ✅ UI components shared via Compose Multiplatform
- ✅ Repository pattern with platform-specific implementations
- ✅ Single source of truth for all business rules

### 4. NO DEBUG ARTIFACTS IN PRODUCTION
- ❌ **NO** `println()`, `console.log()`, or debug print statements
- ❌ **NO** hardcoded test data or development shortcuts  
- ❌ **NO** `// TODO:` comments left in production code
- ✅ Use proper logging framework (Napier) for production logging
- ✅ Remove all development artifacts before committing
- ✅ Clean, production-ready code only

### 5. SELF-DOCUMENTING CODE
- ❌ **NO** explanatory comments in code
- ✅ Code must be self-explanatory through naming
- ✅ Clear class names, function names, variable names
- ✅ Well-structured code that tells its own story
- ✅ Use meaningful domain language in naming
- ✅ Prefer longer descriptive names over comments

### 6. DRY PRINCIPLE (Don't Repeat Yourself)
- ❌ **NO** duplicate business logic across platforms
- ❌ **NO** copy-pasted code blocks
- ✅ Extract common functionality to shared modules
- ✅ Single implementation of business rules
- ✅ Platform-specific adapters only when necessary
- ✅ Reusable components and utilities

### 7. NO TESTING FRAMEWORK
- ❌ **NO** unit tests, integration tests, or test frameworks
- ❌ **NO** test dependencies in build.gradle
- ❌ **NO** test directories (commonTest, androidTest, iosTest)
- ✅ **REAL TESTING ONLY** - Test with actual devices and real data
- ✅ Manual QA and user acceptance testing
- ✅ Production deployment validation

## 🤖 MCP SERVER INTEGRATION (MANDATORY)

### Perplexity Research Requirements
- ✅ **ALWAYS** use `mcp__perplexity-mcp__perplexity_search_web` before implementing complex features
- ✅ **RESEARCH FIRST** - Investigate best practices, libraries, and solutions before coding
- ✅ **VALIDATE APPROACHES** - Confirm technical decisions with current industry standards
- ✅ **AVOID REINVENTING** - Find existing solutions and adapt them intelligently
- ❌ **NO** implementing complex features without prior research
- ❌ **NO** guessing at technical solutions when research is available

### Taskmaster Project Management (MANDATORY SETUP)
- ✅ **CRITICAL**: Taskmaster MCP requires proper configuration in 2025
- ✅ **API Keys**: Set `ANTHROPIC_API_KEY` environment variable for AI model access
- ✅ **Configuration**: Create `.mcp.json` with Taskmaster server config in project root
- ✅ **Tag-based Sessions**: Use tag-attached project context for isolation
- ✅ **Task Files**: Store tasks in `.taskmaster/tasks/tasks.json` structure
- ✅ **Always initialize** with `mcp__task-master-ai__initialize_project` first
- ✅ **Parse PRD** using `mcp__task-master-ai__parse_prd` to generate tasks
- ✅ **Track everything** with real-time status updates
- ❌ **NO** manual task tracking outside of Taskmaster
- ❌ **NO** working without proper MCP configuration

### Research-Driven Development Workflow
1. **Identify complexity** - Recognize when a feature requires research
2. **Research thoroughly** - Use Perplexity to investigate solutions
3. **Document findings** - Store research results in Taskmaster tasks
4. **Implement cleanly** - Apply Clean Code principles to the solution
5. **Validate results** - Ensure implementation matches research recommendations

### MCP Configuration Requirements (2025)
Taskmaster MCP is configured globally in Claude Code. No local `.mcp.json` file is needed.

**Global MCP Setup:**
- Taskmaster MCP server runs globally via Claude Code
- API keys are configured in Claude Code's global settings
- Project-specific context via `projectRoot` parameter
- Tag-based session isolation via `.taskmaster/state.json`
- Tasks stored in `.taskmaster/tasks/tasks.json` structure

**Note:** Do NOT create a local `.mcp.json` file as it will create duplicate MCP instances.

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

## 🤖 AI FEATURES ARCHITECTURE

### Local-First AI Processing
- ✅ **On-device Whisper model** for transcription privacy
- ✅ **TensorFlow Lite** for local ML inference
- ✅ **Local vector database** for semantic search
- ✅ **Privacy-centric approach** - no cloud processing of personal data
- ❌ **NO** sending user content to external AI services without consent
- ✅ Fallback to cloud only for non-personal data processing

### AI Feature Implementation
- ✅ **Transcription** - Local Whisper integration for episode transcripts
- ✅ **Semantic Search** - Natural language queries across content
- ✅ **Smart Recommendations** - ML-based content discovery
- ✅ **Audio Enhancement** - AI-optimized playback speed and noise reduction
- ✅ **Content Analysis** - Chapter detection and quote extraction
- ✅ All AI features must work offline with local models

## 📱 NAVIGATION ARCHITECTURE (MANDATORY)

### Core Navigation Structure
- ✅ **EXACTLY 3 MAIN TABS** - No more, no less
- ✅ **Bottom navigation** with consistent icons and labels
- ❌ **NO DUPLICATE FUNCTIONALITY** between tabs
- ❌ **NO NESTED TABS** within main navigation

### Tab 1: Discover
- ✅ **Search bar** at top for podcast discovery
- ✅ **Content type tabs** below search: Popular, Recommendations, New
- ✅ **Genre-based sections** with horizontal scrolling podcast lists
- ✅ **Real iTunes API data** with actual podcast content
- ❌ **NO** separate search tab - search is integrated here

### Tab 2: New Episodes  
- ✅ **NEW episodes from user's subscribed podcasts** - Recent/unplayed episodes only
- ✅ **Sorted by publish date** (newest first)
- ✅ **Episode cards** with podcast info, title, description, duration
- ✅ **Play button** integration with audio player
- ✅ **Mark as played/unplayed** functionality
- ✅ **Empty state**: "No new episodes from your subscriptions"
- ❌ **NO** episodes from non-subscribed podcasts
- ❌ **NO** old/played episodes - only fresh content

### Tab 3: Library
- ✅ **ALL subscribed podcasts** - Complete user collection
- ✅ **Grid layout** with podcast covers and titles
- ✅ **Quick access** to full podcast details and episode list
- ✅ **Subscription management** (unsubscribe from here)
- ✅ **Download management** for offline episodes
- ✅ **Empty state**: "Your subscribed podcasts will appear here"
- ❌ **NO** discovery content - only user's subscriptions

### Navigation Rules
- ✅ **Single responsibility** per tab - no functional overlap
- ✅ **Intuitive user flow** - discovery → subscription → consumption
- ✅ **Consistent UI patterns** across all tabs
- ✅ **Deep linking support** for podcast and episode details

## 🔐 Security Requirements

### API Security
- ✅ **Environment-based API key management** (no hardcoded keys)
- ✅ Certificate pinning for network requests
- ✅ Input validation and sanitization
- ✅ Secure storage for sensitive data
- ❌ **NO** API keys in source code or version control
- ✅ Google Auth integration for secure Drive sync

### Data Protection
- ✅ Encrypt user preferences and sensitive data
- ✅ Secure database transactions
- ✅ Handle network failures gracefully
- ✅ Validate all external data sources
- ✅ Privacy-first approach for all user data

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

### Pragmatic Testing Approach
- ✅ **Test critical paths only** - Focus on business-critical functionality
- ✅ **Real integration tests** - Test with actual APIs and services
- ✅ **Manual testing first** - Validate features work before writing tests
- ❌ **NO** tests for tests' sake - Avoid unnecessary test coverage
- ❌ **NO** mocked business logic tests
- ✅ Write tests only when they add real value and prevent regressions

### Code Quality
- ✅ Kotlin coding conventions
- ✅ Consistent formatting and style
- ✅ Error handling for all external dependencies
- ✅ Proper resource management and cleanup
- ✅ Thread safety for shared state
- ✅ Focus on code correctness over test coverage metrics

## 🎨 UI/UX Standards

### OrangeCast Design System
- ✅ **Soft Orange Accent Strategy** - Orange (#FF8A50) for primary actions and key navigation
- ✅ **Secondary Orange (#FFB380)** - For hover states and subtle highlights
- ✅ **Minimalist Clarity** - Clean interface prioritizing content over visual complexity
- ✅ **Content-First Typography** - Clear fonts that don't compete with orange accents
- ✅ **Generous Whitespace** - Breathing room to reduce cognitive load
- ✅ **Material Design 3** with OrangeCast design language adaptations

### Cross-Platform Consistency
- ✅ 95%+ identical user experience on both platforms
- ✅ Shared UI components via Compose Multiplatform
- ✅ Platform-specific adaptations only for native feel
- ✅ Consistent navigation patterns
- ✅ Unified design system and theming
- ✅ Dark/light theme support with adjusted orange opacity

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
- ✅ WCAG 2.1 AA compliance target

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

## 📝 TASKMASTER INTEGRATION

### Task Management Requirements
- ✅ **Primary Source**: Taskmaster MCP server for ALL task tracking
- ✅ **Initialize First**: Run `mcp__task-master-ai__initialize_project` at project start
- ✅ **Parse PRD**: Use `mcp__task-master-ai__parse_prd` to generate tasks from PRD
- ✅ **Track Everything**: All work must be tracked in Taskmaster
- ❌ **NO** manual task files or external tracking

### Task Execution Workflow
1. **Get next task**: `mcp__task-master-ai__next_task`
2. **Set status**: `mcp__task-master-ai__set_task_status` to "in-progress"
3. **Research if complex**: Use Perplexity MCP for research
4. **Implement**: Follow Clean Code principles
5. **Complete**: Update status to "done" immediately
6. **Document**: Add findings to task with `mcp__task-master-ai__update_task`

### Task Priority Rules
- ✅ **Dependencies first** - Respect task dependency chains
- ✅ **Complexity analysis** - Use `mcp__task-master-ai__analyze_project_complexity`
- ✅ **Expand complex tasks** - Use `mcp__task-master-ai__expand_task` for subtasks
- ✅ **Real-time updates** - Status changes immediately after work
- ❌ **NO** working on tasks out of order without justification

### Research Integration
- ✅ **Complex features** require research via `mcp__perplexity-mcp__perplexity_search_web`
- ✅ **Store research** in tasks using `mcp__task-master-ai__update_task`
- ✅ **Reference research** when implementing solutions
- ✅ **Validate approaches** against research findings
- ❌ **NO** implementing without research for complex features

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

**Remember: These rules are non-negotiable. Every line of code must comply with these standards to maintain the project's high quality and cross-platform consistency. Taskmaster MCP is the single source of truth for development priorities and task management.**

## Task Master AI Instructions
**Import Task Master's development workflow commands and guidelines, treat as if import is in the main CLAUDE.md file.**
@./.taskmaster/CLAUDE.md
