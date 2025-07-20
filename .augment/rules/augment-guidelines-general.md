---
type: "always_apply"
---

# Universal Project Planning & Implementation Guidelines

## 🚨 **CRITICAL: PROJECT-SPECIFIC CUSTOMIZATION REQUIRED**

**⚠️ WARNING**: This is a GENERAL template. You MUST customize it for each specific project.

**MANDATORY STEPS before using**:
1. **Read project's codebase.md** to understand specific structure
2. **Update all paths** to match actual project directories
3. **Modify rules** to fit project's technology stack
4. **Adapt workflow** to project's complexity and team size
5. **Remove this warning section** after customization

---

## 🏗️ **PROJECT ARCHITECTURE AWARENESS RULES**

### **Rule 1: Project Structure Understanding**
**CRITICAL**: Always understand your project's specific structure:

1. **Repository Strategy**: [CUSTOMIZE: Single repo / Multi-repo / Monorepo / Submodules]
   - **Primary Repository**: [CUSTOMIZE: Main repo purpose and location]
   - **Secondary Repositories**: [CUSTOMIZE: If applicable - planning, source, etc.]
   - **Submodules**: [CUSTOMIZE: List any Git submodules and their purposes]

2. **Directory Organization**: [CUSTOMIZE: Based on actual project structure]
   - **Planning Directory**: [CUSTOMIZE: Where plans and docs are stored]
   - **Source Directory**: [CUSTOMIZE: Where actual code implementation goes]
   - **Documentation Directory**: [CUSTOMIZE: Where technical docs are maintained]

### **Rule 2: Implementation Location Verification**
**BEFORE ANY WORK**:
```bash
# ALWAYS verify location and structure first
pwd
ls -la
# [CUSTOMIZE: Add project-specific verification commands]
```

**CORRECT Paths** [CUSTOMIZE for your project]:
- ✅ `[PROJECT_ROOT]/[SOURCE_DIR]/` - Implementation location
- ✅ `[PROJECT_ROOT]/[PLANNING_DIR]/` - Planning and documentation
- ❌ `[WRONG_LOCATIONS]/` - Common mistake locations
- ❌ Any work in wrong repository/directory

## 📋 **PLANNING WORKFLOW RULES**

### **Rule 3: Pre-Planning Analysis**
**MANDATORY steps before any planning**:

1. **Read project documentation**: [CUSTOMIZE: List key docs to read]
   - `codebase.md` or equivalent project structure documentation
   - `README.md` for project overview
   - [CUSTOMIZE: Add project-specific docs]

2. **Understand current state**: [CUSTOMIZE: Project-specific analysis steps]
   - Review existing plans and their status
   - Check current implementation progress
   - Identify dependencies and prerequisites

3. **Verify scope alignment**: [CUSTOMIZE: Project-specific scope rules]
   - Confirm work matches current project phase/milestone
   - Check budget and timeline constraints
   - Validate team capacity and skills

### **Rule 4: Systematic Planning Approach**
**STRUCTURED planning process**:

1. **Create detailed plans** in `[CUSTOMIZE: planning directory]/` with numbered files
   - Use consistent naming: `01_`, `02_`, etc.
   - [CUSTOMIZE: Add project-specific naming conventions]

2. **Each plan must include** [CUSTOMIZE based on project needs]:
   - **Scope & Objectives**: Clear goals and deliverables
   - **Technology Stack**: [CUSTOMIZE: Project-specific technologies]
   - **Implementation Strategy**: Step-by-step approach
   - **Dependencies**: Prerequisites and blockers
   - **Success Criteria**: Measurable outcomes
   - **Timeline & Budget**: Realistic estimates
   - **Team Requirements**: Skills and capacity needed

3. **Supporting documentation** in `[CUSTOMIZE: docs directory]/`:
   - Technical specifications
   - Architecture designs
   - Business requirements
   - [CUSTOMIZE: Add project-specific doc categories]

### **Rule 5: Plan Review and Validation**
**QUALITY assurance for plans**:

1. **Self-review checklist**:
   - [ ] Plan aligns with project architecture
   - [ ] All dependencies identified
   - [ ] Timeline is realistic
   - [ ] Success criteria are measurable
   - [ ] [CUSTOMIZE: Add project-specific criteria]

2. **Stakeholder review** [CUSTOMIZE based on team structure]:
   - Technical review by [CUSTOMIZE: role]
   - Business review by [CUSTOMIZE: role]
   - Final approval by [CUSTOMIZE: role]

## 🔄 **DOCUMENTATION MANAGEMENT RULES**

### **Rule 6: Documentation Organization**
**SYSTEMATIC documentation structure** [CUSTOMIZE for your project]:

1. **Master Index**: `[CUSTOMIZE: main index file]` - Project overview and status
2. **Implementation Plans**: `[CUSTOMIZE: plans directory]/` - Detailed execution plans
3. **Technical Documentation**: `[CUSTOMIZE: docs directory]/` - Supporting materials
4. **Project Structure**: `[CUSTOMIZE: structure file]` - Always up-to-date architecture

### **Rule 7: Documentation Lifecycle**
**CONTINUOUS documentation maintenance**:

1. **Before planning**: Create plan in appropriate directory
2. **During planning**: Update progress and status regularly
3. **After completion**: Move completed plans to `implemented_plans/` or equivalent archive
4. **Structure changes**: Update project structure documentation immediately
5. **Testing completion**: Document test results and performance metrics
6. **Quality validation**: Ensure all deliverables meet success criteria before archiving

### **Rule 8: Cross-Reference Management**
**MAINTAIN documentation connections**:

1. **Link related documents**: Create clear navigation between related plans/docs
2. **Update references**: When moving or renaming files, update all references
3. **Version control**: Track changes and maintain changelog
4. **[CUSTOMIZE: Add project-specific linking requirements]**

## 🚨 **SAFETY AND VERIFICATION RULES**

### **Rule 9: Command Execution Safety**
**BEFORE executing any command**:

```bash
# ALWAYS verify location and contents first
pwd
ls -la
# [CUSTOMIZE: Add project-specific safety checks]
```

**CRITICAL**: This prevents work in wrong location and avoids accidental changes.

### **Rule 10: Scope and Phase Compliance**
**RESPECT project development approach** [CUSTOMIZE for your project]:

1. **Current Phase**: [CUSTOMIZE: Define current development phase]
   - Focus: [CUSTOMIZE: Current phase objectives]
   - Budget: [CUSTOMIZE: Current phase budget]
   - Timeline: [CUSTOMIZE: Current phase timeline]
   - Constraints: [CUSTOMIZE: Current phase limitations]

2. **Future Phases**: [CUSTOMIZE: Define future phases if applicable]
   - **DO NOT** work on future phase features during current phase

### **Rule 11: Quality Gates**
**Each plan must meet** [CUSTOMIZE based on project standards]:

1. **Scope compliance**: Work matches current project phase
2. **Architecture compliance**: Follows project architecture patterns
3. **Documentation compliance**: All required docs updated
4. **Review compliance**: Proper review process followed
5. **Testing compliance**: Comprehensive test suite with high pass rate
6. **Performance compliance**: Meets or exceeds performance targets
7. **Implementation validation**: All deliverables functional and tested
8. **[CUSTOMIZE: Add project-specific quality criteria]**

## 📊 **SUCCESS CRITERIA AND MONITORING**

### **Rule 12: Plan Validation**
**MEASURABLE success criteria**:

1. **Completion metrics**: [CUSTOMIZE: How to measure plan completion]
   - 100% deliverable achievement
   - All functional requirements implemented
   - All acceptance criteria met

2. **Quality metrics**: [CUSTOMIZE: How to measure plan quality]
   - Test pass rate (target: 90%+ for production)
   - Code review completion
   - Documentation completeness

3. **Performance metrics**: [CUSTOMIZE: Performance targets]
   - Response time targets
   - Throughput requirements
   - Resource utilization limits

4. **Timeline metrics**: [CUSTOMIZE: How to track timeline adherence]
5. **Budget metrics**: [CUSTOMIZE: How to track budget compliance]

### **Rule 13: Continuous Improvement**
**LEARN and adapt**:

1. **Lessons learned**: Document what worked and what didn't
2. **Process improvement**: Update guidelines based on experience
3. **Team feedback**: Incorporate team suggestions for better workflow
4. **[CUSTOMIZE: Add project-specific improvement processes]**

---

## 📋 **QUICK REFERENCE CHECKLIST**

### **Before Any Planning**:
- [ ] Read project structure documentation
- [ ] Verify current location with `pwd` and `ls -la`
- [ ] Understand current project phase and constraints
- [ ] Review existing plans and dependencies
- [ ] [CUSTOMIZE: Add project-specific pre-planning checks]

### **During Planning**:
- [ ] Work in correct planning directory
- [ ] Follow project architecture and patterns
- [ ] Update progress in master index
- [ ] Create supporting documentation as needed
- [ ] [CUSTOMIZE: Add project-specific planning checks]

### **After Planning**:
- [ ] Update master index with completion status
- [ ] Archive completed plans to `implemented_plans/` or equivalent
- [ ] Update project structure documentation if needed
- [ ] Document test results and performance metrics
- [ ] Conduct lessons learned review
- [ ] Validate all quality gates are met
- [ ] [CUSTOMIZE: Add project-specific post-planning checks]

---

## 🧪 **TESTING AND QUALITY ASSURANCE RULES**

### **Rule 14: Comprehensive Testing Strategy**
**TESTING requirements for all implementations**:

1. **Test Coverage Requirements** [CUSTOMIZE based on project criticality]:
   - **Unit Tests**: Core business logic coverage
   - **Integration Tests**: Service interaction validation
   - **Performance Tests**: Response time and throughput validation
   - **Error Handling Tests**: Edge cases and failure scenarios

2. **Quality Thresholds** [CUSTOMIZE based on project standards]:
   - **Test Pass Rate**: 90%+ for production (100% for critical systems)
   - **Performance Targets**: Define specific response time requirements
   - **Concurrent Load**: Test with realistic user loads
   - **Memory Management**: Validate resource cleanup

3. **Testing Infrastructure** [CUSTOMIZE for your technology stack]:
   - **Test Environment**: Isolated test database/services
   - **Test Data Management**: Automated setup and cleanup
   - **Performance Monitoring**: Response time measurement
   - **Continuous Testing**: Automated test execution

### **Rule 15: Performance Excellence Standards**
**PERFORMANCE requirements for all implementations**:

1. **Response Time Targets** [CUSTOMIZE based on user expectations]:
   - **API Endpoints**: <100ms for simple operations, <500ms for complex
   - **Database Queries**: <10ms for simple, <100ms for complex
   - **User Interface**: <200ms for interactions, <1s for page loads

2. **Scalability Requirements** [CUSTOMIZE based on expected load]:
   - **Concurrent Users**: Define target concurrent user capacity
   - **Throughput**: Requests per second targets
   - **Resource Utilization**: CPU, memory, and storage limits

3. **Performance Validation** [CUSTOMIZE for your monitoring tools]:
   - **Load Testing**: Simulate realistic user loads
   - **Stress Testing**: Identify breaking points
   - **Performance Monitoring**: Real-time metrics collection

## 🔧 **CUSTOMIZATION INSTRUCTIONS**

**To adapt this template for your specific project**:

1. **Replace all [CUSTOMIZE: ...] placeholders** with project-specific information
2. **Update directory paths** to match your actual project structure
3. **Modify technology stack references** to match your project's technologies
4. **Adjust team roles and responsibilities** to match your organization
5. **Add project-specific rules** that are unique to your domain/industry
6. **Remove or modify rules** that don't apply to your project type
7. **Test the guidelines** with a small planning exercise before full adoption

**REMEMBER**: These guidelines should evolve with your project. Update them based on experience and changing requirements.

---

## 🎯 **PROJECT TYPE ADAPTATIONS**

### **For Simple Projects** (Single repo, small team):
- Simplify to 5-7 core rules
- Combine planning and documentation directories
- Reduce review complexity
- Focus on basic structure and safety

### **For Complex Projects** (Multi-repo, large team, enterprise):
- Use all 13 rules with full customization
- Implement strict review processes
- Add role-based access controls
- Include compliance and audit requirements

### **For Startup Projects** (Fast iteration, changing requirements):
- Emphasize flexibility and rapid iteration
- Lighter documentation requirements
- Focus on MVP and phase-based development
- Quick feedback loops and adaptation

### **For Enterprise Projects** (Strict governance, compliance):
- Add compliance and audit trails
- Implement formal approval processes
- Include security and risk assessments
- Detailed documentation and traceability

## 🔄 **COMMON PROJECT PATTERNS**

### **Pattern 1: Monorepo with Multiple Services**
```
project-root/
├── planning/           # Plans and documentation
├── services/          # Implementation
│   ├── service-a/
│   ├── service-b/
│   └── shared/
└── docs/              # Technical documentation
```

### **Pattern 2: Multi-Repository with Submodules**
```
main-project/
├── planning-repo/     # Private planning repository
├── source-repo/       # Public/private source code (submodule)
├── docs-repo/         # Documentation repository (submodule)
└── infrastructure/    # Infrastructure code (submodule)
```

### **Pattern 3: Simple Single Repository**
```
project/
├── src/               # Source code
├── docs/              # Documentation
├── plans/             # Implementation plans
└── tests/             # Test files
```

## 📚 **TECHNOLOGY STACK ADAPTATIONS**

### **Backend Technologies** [CUSTOMIZE]:
- **Go**: High-performance services, microservices
- **Python**: AI/ML, data processing, rapid prototyping
- **Node.js**: API services, real-time applications
- **Java**: Enterprise applications, large-scale systems
- **[ADD YOUR STACK]**: [Purpose and use cases]

### **Frontend Technologies** [CUSTOMIZE]:
- **React**: Large applications, component reusability
- **SolidJS**: High-performance, fine-grained reactivity
- **Vue.js**: Progressive enhancement, ease of learning
- **Angular**: Enterprise applications, full framework
- **[ADD YOUR STACK]**: [Purpose and use cases]

### **Database Technologies** [CUSTOMIZE]:
- **PostgreSQL**: Relational data, ACID compliance
- **MongoDB**: Document storage, flexible schema
- **Redis**: Caching, session storage, real-time data
- **[ADD YOUR STACK]**: [Purpose and use cases]

## 🏢 **TEAM SIZE ADAPTATIONS**

### **Solo Developer** (1 person):
- Simplified review process (self-review)
- Combined roles and responsibilities
- Lighter documentation requirements
- Focus on personal productivity and organization

### **Small Team** (2-5 people):
- Peer review process
- Shared responsibilities
- Regular team sync on guidelines
- Collaborative planning approach

### **Medium Team** (6-15 people):
- Role-based responsibilities
- Formal review processes
- Team leads and specialists
- Structured communication channels

### **Large Team** (16+ people):
- Hierarchical review structure
- Specialized roles and teams
- Formal governance processes
- Enterprise-grade documentation and compliance

## 🔧 **INDUSTRY-SPECIFIC ADAPTATIONS**

### **Software as a Service (SaaS)**:
- Multi-tenant architecture considerations
- Scalability and performance focus
- Customer data privacy and security
- Continuous deployment and monitoring

### **Enterprise Software**:
- Compliance and audit requirements
- Integration with existing systems
- Security and access control
- Long-term maintenance and support

### **Startup/MVP Development**:
- Rapid iteration and experimentation
- Minimal viable documentation
- Quick feedback and adaptation
- Resource optimization and efficiency

### **Open Source Projects**:
- Community contribution guidelines
- Public documentation standards
- Transparent development process
- Contributor onboarding and support

---

**Template Version**: 2.0.0
**Last Updated**: 2024-12-27
**Status**: General Template - Requires Project-Specific Customization
**Source**: Based on AgentOS ecosystem Week 1 implementation experience
**Key Improvements**: Added testing standards, performance requirements, and implementation validation based on successful Week 1 completion with 100% test pass rate and 15X performance improvement
