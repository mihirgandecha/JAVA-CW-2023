**Username**: [yk23504]

### Continuous Integration & Continuous Automation:
Setup such that build will not merge unless error is managed (CA), and JUnit test produces error report shown on Actions, pinpointing JUnit failed case:

**Problems Faced:** Maven build shows JUnit result at end. Automated this process by adding build test to .yml after Maven process, then generating unit test using marketplace plugin (researched which worked with maven)

#### Example:

![Screenshot](resources%20/CI_CA.png)