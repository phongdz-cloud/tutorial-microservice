# JaCoCo Code Coverage Setup

This document explains the JaCoCo code coverage configuration for the user-service Spring Boot application.

## Configuration Overview

The JaCoCo Maven plugin has been configured with the following settings:

### Coverage Thresholds
- **Line Coverage**: 80% minimum
- **Branch Coverage**: 70% minimum  
- **Method Coverage**: 75% minimum

### Excluded Packages
The following packages are excluded from coverage analysis:
- `com.example.dto.**` - Data Transfer Objects
- `com.example.config.**` - Configuration classes
- `com.example.entity.**` - JPA entities
- `com.example.exception.**` - Exception classes
- `com.example.UserServiceApplication` - Main application class

### Report Generation
- **HTML Report**: `target/site/jacoco/index.html`
- **XML Report**: `target/site/jacoco/jacoco.xml` (for SonarQube integration)

## Maven Commands

### Run Tests with Coverage
```bash
# Run all tests with coverage analysis
mvn clean test

# Run tests with coverage and generate reports
mvn clean test jacoco:report

# Run tests with coverage and verify thresholds
mvn clean test jacoco:check
```

### View Coverage Reports
```bash
# Generate and view HTML report
mvn jacoco:report
open target/site/jacoco/index.html

# On Windows:
start target/site/jacoco/index.html

# On Linux:
xdg-open target/site/jacoco/index.html
```

### Skip Coverage for Faster Development
```bash
# Skip coverage analysis for faster builds
mvn clean test -Djacoco.skip=true

# Or skip only the coverage check (still generate reports)
mvn clean test -Djacoco.check.skip=true
```

### Advanced Commands
```bash
# Generate coverage report only (without running tests)
mvn jacoco:report

# Check coverage thresholds only
mvn jacoco:check

# Generate coverage for specific test class
mvn test -Dtest=UserServiceImplTest jacoco:report

# Generate coverage with specific Maven profile
mvn clean test jacoco:report -Pcoverage
```

## Plugin Execution Phases

The JaCoCo plugin is configured to run in three phases:

1. **prepare-agent** (initialize phase): Prepares the JaCoCo agent for test execution
2. **report** (test phase): Generates coverage reports after tests complete
3. **check** (verify phase): Verifies coverage thresholds and fails build if not met

## Integration with CI/CD

### GitHub Actions Example
```yaml
- name: Run tests with coverage
  run: mvn clean test jacoco:report

- name: Upload coverage reports
  uses: codecov/codecov-action@v3
  with:
    file: ./target/site/jacoco/jacoco.xml
```

### Jenkins Pipeline Example
```groovy
stage('Test with Coverage') {
    steps {
        sh 'mvn clean test jacoco:report'
        publishHTML([
            allowMissing: false,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: 'target/site/jacoco',
            reportFiles: 'index.html',
            reportName: 'JaCoCo Coverage Report'
        ])
    }
}
```

## Troubleshooting

### Common Issues

1. **Build fails due to low coverage**
   - Check the coverage report to identify uncovered code
   - Add more tests or adjust thresholds if appropriate
   - Use `-Djacoco.check.skip=true` to skip threshold checking

2. **Reports not generated**
   - Ensure tests are run before report generation
   - Check that the `prepare-agent` execution ran successfully
   - Verify the output directory exists

3. **Exclusions not working**
   - Ensure exclusion patterns match the actual package structure
   - Use forward slashes in exclusion patterns (e.g., `com/example/dto/**`)

### Debug Commands
```bash
# Show detailed plugin execution
mvn clean test -X

# Show only JaCoCo plugin output
mvn clean test -Djacoco.verbose=true
```

## Best Practices

1. **Regular Coverage Monitoring**: Run coverage checks regularly during development
2. **Meaningful Thresholds**: Set realistic thresholds based on project requirements
3. **Selective Exclusions**: Only exclude classes that don't need testing (DTOs, configs, etc.)
4. **CI Integration**: Include coverage checks in your CI/CD pipeline
5. **Team Standards**: Establish team standards for coverage requirements

## Additional Resources

- [JaCoCo Maven Plugin Documentation](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [JaCoCo Ant Tasks Reference](https://www.jacoco.org/jacoco/trunk/doc/ant.html)
- [SonarQube Integration Guide](https://docs.sonarqube.org/latest/analysis/coverage/)
