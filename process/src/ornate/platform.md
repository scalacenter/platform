# What is the Scala Platform

The Scala Platform is a stable collection of libraries with widespread
use and a low barrier to entry for beginners and intermediate users. Its
goal is to provide a smooth and productive programming experience right
from the start, while ensuring every module's stability and production
readiness.

The Platform consists of several independent modules that solve specific
problems. Committers and developers of each module work together to
develop and maintain them. Committers also review contributions from any
Scala developer. For encouraging more contributions from companies and
industrial users, modules have clear development workflows, that are
both predictable and easy to follow. The Scala community sets the overall
direction of the Platform.

## What is a Platform module?

A Platform module is a library of the Scala Platform that is characterized by
stability, widespread use and a low barrier to entry for beginners and
intermediate users.

These features are the cornerstone of the Scala Platform process.
While widespread use ensures that a library is fulfilling a need of the Community,
the low barrier to entry helps Scala developers use it for both learning
purposes and production systems.

# What is the Process

> {.note}
> The following document is a draft and it's open for discussion. For
> more information, check the [public announcement of its pre-release](https://internals.scala-lang.org/t/early-release-of-the-scala-platform-process/95).

The Scala Platform Process provides organizational support for a broad
range of open source software projects. The mission of the process is to
provide high-quality software for the good of the Scala community.
Through a collaborative and meritocratic development process, the
Platform delivers a **stable** collection of libraries with widespread
use and a low barrier to entry for beginners and intermediate users, ready
for serious production use.

The following specification is inspired by the
[Apache process](http://incubator.apache.org/index.html) and the
[Rust release model](https://blog.rust-lang.org/2014/12/12/1.0-Timeline.html).
Other initiatives like the [Haskell Platform](https://www.haskell.org/platform/) and the
[Boost library](http://www.boost.org/) are also prominent inspiration sources. The resulting
document has been drafted by the Scala Center and improved by the Platform
Committee members, that have helped shape the essence of the process
with their curated feedback and suggestions.

### Expectations

The Scala Platform process sets concrete expectations for the stability
and compatibility of the selected modules. Although the specified
policies have strict compatibility requirements, it is impossible to
guarantee that no future change will break any program.

The Scala Platform process provides concrete policies regarding:

1.  **Security**. A security issue in the specification or
    implementation may come to light whose resolution requires
    breaking compatibility. Modules maintainers’ reserve the right to
    address such security issues.
2.  **Experimental behavior**. There are some aspects of the modules
    that may be undefined or experimental. Programs that depend on
    such unspecified behavior may break in future releases.
3.  **Specification errors**. If it becomes necessary to address an
    inconsistency or incompleteness in the specification, resolving
    the issue could affect the meaning of existing programs. Modules
    maintainers’ reserve the right to address such issues, including
    updating the implementations. Except for security issues, no
    incompatible changes to the specification would be made.
4.  **Bugs**. If a compiler or library has a bug that violates the
    specification, a program that depends on the buggy behavior may
    break if the bug is fixed. Modules maintainers’ reserve the right
    to fix such bugs.

Needless to say that modules maintainers’ will update specification and
libraries to avoid affecting existing code, whenever it’s possible.
These same considerations apply to successive point releases and are
later explained in the release and stability policies. Aside from
compatibility, no guarantee can be made about the performance of a given
program between different Platform releases.

#### What stability guarantees can I expect?

The expectations are concretized in the [Scala Platform policies](#release).

## Committee Members

The Scala Platform Committee is exclusively composed of members of the Scala community.
Through a democratic process, they set the direction of the Platform, review
proposals from the community, and decide on the modules that join the Platform.

The members of the Committee are dedicated developers with a long record of
contributions to the Scala ecosystem, the development of the language and major
open-source projects. They have been elected by the Scala Center to represent and
serve with their experience the broad interest of the Scala community, not their
personal or employers' viewpoints.

The Committee members are:

-   Dale Wijnand ([@dwijnand](https://github.com/dwijnand))
-   Aleksandar Prokopec ([@axel22](https://github.com/axel22))
-   Lars Hupel ([@larsrh](https://github.com/larsrh))
-   Marius Eriksen ([@mariusae](https://github.com/mariusae))
-   Bill Venners ([@bvenners](https://github.com/bvenners))
-   Konrad Malawski ([@ktoso](https://github.com/ktoso))
-   Pathikrit Bhowmick ([@pathikrit](https://github.com/pathikrit))
-   Alexander Podkhalyuzin ([@Alefas](https://github.com/Alefas))
-   Mathias Doenitz ([@sirthias](https://github.com/sirthias))
-   Rex Kerr ([@Ichoran](https://github.com/Ichoran))
-   David Hall ([@dlwh](https://github.com/dlwh))

Along with the Committee members, the SPP Process lead manages the process,
schedules meetings and helps bootstrap the Platform. The current Process Lead
is Jorge Vicente Cantero ([@jvican](https://github.com/jvican)).
