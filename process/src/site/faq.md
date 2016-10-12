# FAQ (Frequent Asked Questions)

If you have a question that it's not here, [send a PR](https://github.com/scalaplatform) or ask it in [Gitter](https://gitter.im).

## Infrastructure

#### My module already has a release process, can I keep it?

You can keep it, and as a maintainer you can even release versions whenever you
want, but you must use the Scala Platform release process and the sbt `platform-release` plugin.

An official release process for all the modules is essential to attract contributors
and industrial users. Concretely:
1. It specifies deadlines to ship bugfixes and features; and
1. It prevents deadlocks in the release of those changes

When those conditions are not met, contributors are discouraged from participating
in the development of a project.

The Scala Platform process ensures reasonable rules
to keep both maintainers and contributors happy, and having a default release process
gives predictability to the contribution process and encourages companies to assign
resources and engineers. Remember how motivated you may feel to know that tomorrow
your changes will be live, or how easy is for companies to assign an engineer to fix
bugs if they know that next week they can depend on the new version.

#### My module has its own CI, can I keep it?

Yes, you can keep it so long as you fulfill the same purposes. The important thing is
not what infrastructure or CI you use, but the fact that you use it. Setting up
our CI may give you some performance benefits, though it's up to you if you need them or not.

## Code of Conduct (CoC)

#### Why shall I conform to Scala Center's CoC?

Contributing to the Platform should be a nice experience for any person. Online
communities need a discrimination-free and healthy environment to succeed,
and require explicit contracts that ensure respectful interactions among their
members. Conforming to the Scala Center Code of Conduct protects you and potential
contributors from bad actors. We cannot build communities without enforcing
civic behaviour. Everyone deserves it.

#### My project already has a *CoC*, can I keep it?

You can keep it, but you also must abide by the Scala Center Code of Conduct.
In case of conflict, the managers of the Platform will enforce the official
Code of Conduct, and then apply yours. However, having two code of conducts is
often pointless. If your code of conduct ensures extra policies, you are
encouraged to proposed the inclusion of those policies in the official Code of
Conduct. We all should define what civic behaviour is.

#### I want to report the violation of the CoC, what shall I do?

Contact the SPP Process Lead ([@jvican](https://github.com/jvican)), or a Scala
Center representative and explain the situation. You can also get in touch with
any Scala Platform Committee member or module maintainers you want.
