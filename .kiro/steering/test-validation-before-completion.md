---
inclusion: always
---

# Test Validation Rule for Task Completion

## Mandatory Test Execution

Before marking any task as "completed", you MUST ensure all existing tests pass by running the test suite.

## Test Execution Steps

1. **Run all tests**: Execute `mvn test` to run the complete test suite
2. **Verify test results**: Ensure all tests pass with no failures or errors
3. **Handle test failures**: If any tests fail, you must fix them before proceeding
4. **Only then mark complete**: Update task status to "completed" only after all tests pass

## Test Command

Use this Maven command to run tests:

```bash
mvn test
```

## When to Apply

This rule applies to:

- Any task completion from `.kiro/specs/*/tasks.md` files
- Before updating task status to "completed"
- Before performing git commit operations
- After implementing any code changes

## Failure Handling

If tests fail:

- **DO NOT** mark the task as completed
- Analyze the test failures and fix the underlying issues
- Re-run tests until all pass
- Only then proceed with task completion
- Inform the user about any test failures and fixes applied

## Exception Cases

The only exceptions to this rule:

- When the project has no existing tests (first-time setup)
- When tests are explicitly broken due to intentional API changes that require test updates
- In such cases, update the failing tests as part of the task completion

## Integration with Git Rule

This rule must be executed BEFORE the git commit rule:

1. Run tests and ensure they pass
2. Mark task as completed
3. Perform git operations (add, commit, push)

This ensures that the repository always maintains a working state with passing tests.
