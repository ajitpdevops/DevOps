# Article Ideas
- Change a default branch
- Generative AI for DevOps - https://www.uptime.build/post/generative-ai-for-devops-career-must-or-overhyped-bust
- 




# Article 1. Steps to change the Github default branch from master to main
## Step 1 
### create main branch locally, taking the history from master
git branch -m master main

## Step 2 
### push the new local main branch to the remote repo (GitHub) 
git push -u origin main

## Step 3
### switch the current HEAD to the main branch
git symbolic-ref refs/remotes/origin/HEAD refs/remotes/origin/main

## Step 4
### change the default branch on GitHub to main
https://docs.github.com/en/github/administering-a-repository/setting-the-default-branch

## Step 5
### delete the master branch on the remote
git push origin --delete master

## Step 6
### Check all branches now
git branch -a 
