# Author: Tyler Nisonoff
#
# Paramter - public branchname
# checkouts out public branch, merges in the branch you were on
# pushes that changes on the public branch, and then checks out the original branch
function ooga(){
  branch=$(git branch | sed -n '/\* /s///p')
  tomerge=$1 
  git checkout $tomerge
  git merge $branch
  git push origin $tomerge
  git checkout $branch
}
ooga
