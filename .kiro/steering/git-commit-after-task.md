---
inclusion: always
---

# Git Commit Rule for Task Completion

## Automatic Git Operations

After completing any task from a spec, you MUST perform the following git operations:

1. **Stage all changes**: `git add .`
2. **Commit with descriptive message**: `git commit -m "Complete task: [task name]"`
3. **Push to remote**: `git push`

## Commit Message Format

Use this format for commit messages:

- `Complete task: [task number] [brief task description]`
- Example: `Complete task: 2. Set up Maven project structure and core interfaces`

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
