eDelphi
=======

eDelfoi is a research program based on Delphi expert method. It is
developed in co-operation with Future Research Center of Turku School
of Economics.

Delphi technique is used for bringing values, new viewpoints and ideas
as a foundation for planning and decision making, i.e. making
qualitative research. The program can also be used for making a
simple, Survey-type of query. The newest version of the program is
called eDelfoi. 

eDelfoi is licensed under GPLv3.

eDelphi has originally been developed by Otavan Opisto. Future development of software was adopted by Metatavu Oy in Septemper 2016. 

Original repository can be found from: https://github.com/otavanopisto/edelphi

Contributing to eDelphi
-----------------------

Here’s what you need to do:

**1. Create an issue and tag it as bug or enhancement, depending on the nature of the issue.**

If it’s a bug, remember to describe how to reproduce it. If it’s a new feature, try to describe your idea as well as you possibly can.

When describing a new feature, best practise is to use "User story" -format: ***"As a [role], i want to [goal] so that [reason]"***

*For example:* ***As a panelist, i want to be able to receive notifications about updates so that i do not need to visit panel to check them.***

If you plan to implement or fix it yourself, assign yourself to the issue. If not, you are done here. Otherwise, keep on reading.

**2. Fork the repository on GitHub**

**3. Create a new branch into the forked project.**

Feature branches are named feature-#issueno-short-desc (e.g. feature-#1234-update-notifications references into issue https://github.com/Metatavu/eDelphi/issues/1234). Hotfix branches (bug branches) are named similarly as hotfix-#issueno-short-desc (e.g. hotfix-#2345-incorrect-color references into issue https://github.com/Metatavu/eDelphi/issues/2345)

Command for creating new branches is as follows: 

    git checkout develop -b feature-#issueno-short-desc

**4. Push your branch into your fork**

    git push https://github.com/user/eDelphi.git BRANCH_NAME

**5. Add the original repository as remote**

    git remote add upstream https://github.com/Metatavu/eDelphi.git

Now you can keep up with development of the original project by pulling changes from its devel branch:

    git pull upstream develop
 
**6. Make changes and commit.**

Please try to use small commits to make the review easier for us.

**7. Merge**

When the feature is done or the bug fixed, you should merge your branch into the eDelfoi develop branch.

First of all pull changes from upstream:

    git pull upstream develop

Resolve all conflicts if you have any.

**8. Test**

Test your changes against merged code and run unit tests

Command to run unit tests is: 

    mvn clean verify -Pit

**9. Push and send a pull request**

Push your code into the GitHub and send a pull request from the feature / hotfix branch into develop branch in GitHub.

**10. Reviev and merge**

Wait for our team to review the changes. If our reviewers (or review systems) find some problems with your pull-request, we inform you by commenting the code.

When pull-request has passed the review process, our team will merge it to the develop -branch and the changes will end up in the product. 

In some rare cases if the code has some major issues, for example major quality issues or the change would be have other undesired effects, the pull-request can be closed without merging. 
