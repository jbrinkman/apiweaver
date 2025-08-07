# Contributing to ApiWeaver

We welcome contributions to ApiWeaver! This document provides guidelines for contributing to the project.

## Code of Conduct

By participating in this project, you agree to abide by our code of conduct:

- Be respectful and inclusive
- Focus on constructive feedback
- Help create a welcoming environment for all contributors
- Report any unacceptable behavior to the project maintainers

## How to Contribute

### Reporting Issues

Before creating an issue, please:

1. Check if the issue already exists in the [issue tracker](https://github.com/jbrinkman/apiweaver/issues)
2. Search closed issues to see if the problem was already resolved
3. Collect relevant information about your environment and the issue

When creating an issue, please include:

- Clear description of the problem
- Steps to reproduce the issue
- Expected vs. actual behavior
- Environment details (Java version, OS, etc.)
- Sample HTML or URLs if relevant (without sensitive information)
- Error messages or logs

### Suggesting Enhancements

Enhancement suggestions are welcome! Please:

1. Check existing issues and discussions
2. Provide a clear description of the enhancement
3. Explain the use case and benefits
4. Consider implementation complexity and maintenance impact

### Contributing Code

#### Getting Started

1. Fork the repository on GitHub
2. Clone your fork locally:

   ```bash
   git clone https://github.com/YOUR_USERNAME/apiweaver.git
   cd apiweaver
   ```

3. Add the upstream repository:

   ```bash
   git remote add upstream https://github.com/jbrinkman/apiweaver.git
   ```

4. Create a feature branch:

   ```bash
   git checkout -b feature/your-feature-name
   ```

#### Development Process

1. **Write Tests First**: Follow test-driven development when possible
2. **Keep Changes Focused**: One feature or fix per pull request
3. **Follow Code Style**: Maintain consistency with existing code
4. **Update Documentation**: Update relevant documentation for your changes
5. **Test Thoroughly**: Ensure all tests pass and add new tests for your changes

#### Code Standards

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods small and focused
- Handle errors appropriately with specific exceptions
- Use SLF4J for logging

#### Testing Requirements

- Write unit tests for new functionality
- Ensure existing tests continue to pass
- Add integration tests for complex features
- Test edge cases and error conditions
- Maintain or improve code coverage

#### Commit Guidelines

Use clear, descriptive commit messages:

```
feat: add support for nested table structures
fix: handle malformed HTML gracefully
docs: update installation instructions
test: add integration tests for table extraction
refactor: simplify property mapping logic
```

Commit message format:

- `feat`: New features
- `fix`: Bug fixes
- `docs`: Documentation changes
- `test`: Test additions or modifications
- `refactor`: Code refactoring without functional changes
- `style`: Code style changes (formatting, etc.)
- `chore`: Build process or auxiliary tool changes

#### Pull Request Process

1. **Update Your Branch**: Rebase against the latest upstream main:

   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run Tests**: Ensure all tests pass:

   ```bash
   mvn clean verify
   ```

3. **Create Pull Request**:
   - Use a descriptive title
   - Reference related issues
   - Describe the changes and their impact
   - Include testing information

4. **Address Feedback**: Respond to code review comments promptly

5. **Squash Commits**: Clean up commit history if requested

#### Pull Request Template

```markdown
## Description
Brief description of the changes

## Related Issues
Fixes #123
Related to #456

## Changes Made
- List of specific changes
- Another change

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed
- [ ] All existing tests pass

## Documentation
- [ ] Code comments updated
- [ ] README updated (if needed)
- [ ] DEVELOPER.md updated (if needed)

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Tests added for new functionality
- [ ] Documentation updated
- [ ] No breaking changes (or clearly documented)
```

### Documentation Contributions

Documentation improvements are always welcome:

- Fix typos or unclear explanations
- Add examples or use cases
- Improve API documentation
- Update setup or usage instructions

### First-Time Contributors

If you're new to open source or this project:

1. Look for issues labeled `good first issue` or `help wanted`
2. Start with small changes to get familiar with the codebase
3. Ask questions if you need clarification
4. Don't hesitate to request help or guidance

## Development Environment

See [DEVELOPER.md](DEVELOPER.md) for detailed setup instructions.

## Recognition

Contributors will be recognized in:

- GitHub contributors list
- Release notes for significant contributions
- Project documentation

## Questions?

If you have questions about contributing:

1. Check existing documentation
2. Search closed issues and discussions
3. Create a new issue with the `question` label
4. Reach out to maintainers

Thank you for contributing to ApiWeaver!
