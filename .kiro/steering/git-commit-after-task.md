---
inclusion: always
---

# Git Commit Rule for Task Completion

## Automatic Git Operations

After completing any task from a spec, you MUST perform the following git operations:

1. **Stage all changes**: `git add .`
2. **Commit with conventional commit message**: `git commit -m "[type]([scope]): [description]"`
3. **Push to remote**: `git push`

## Commit Message Format

Use conventional commit format for all commit messages:

- **Format**: `[type]([scope]): [description]`
- **Type**: Use `feat` for new features, `fix` for bug fixes, `refactor` for code refactoring, `test` for adding tests, `docs` for documentation, `chore` for maintenance tasks
- **Scope**: Use the task number (e.g., `task-2`, `task-3-1`)
- **Description**: Brief description of what was implemented (lowercase, no period)

### Examples

- `feat(task-2): set up maven project structure and core interfaces`
- `feat(task-3-1): implement http client for url fetching`
- `test(task-4-2): add unit tests for property definition validation`
- `refactor(task-5): optimize openapi property mapping logic`

## When to Apply

This rule applies to:

- Any task completion from `.kiro/specs/*/tasks.md` files
- After updating task status to "completed"
- Before informing the user that the task is finished

## Error Handling

If git operations fail:

- Inform the user about the git error
- Provide the specific error message
- Continue with task completion notification
- Do not block task completion due to git issues

## Repository Setup

Ensure the repository has:

- A remote origin configured
- Proper authentication for pushing
- The current branch is tracking a remote branch

This ensures all progress is automatically saved and backed up to the remote repository.
