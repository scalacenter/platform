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

#### Is the release bot using Platform's Sonatype organization for releasing?

The release bot only publishes NIGHTLYs and BETAs with the Scala Platform sonatype
credentials. For stable releases, we want to give credit to the original authors or
community that contributed to the Platform. For that, we allow module maintainers to
manually release stable releases under their own Sonatype namespace. The release bot
will notify all the module maintainers one week ago before the release has to be cut.
The platform JAR and artifact will depend on those versions, so you must release on time.

> {.note}
> If you prefer that we release stable releases, you can provide your Sonatype user
> and password as secrets in the CI, and the release bot will take care of the rest.

#### I want to use Bintray, what can I do?

At this moment there's not support for Bintray. If several module maintainers ask for it,
we can consider releasing to Bintray instead of Sonatype, but this features is not a first priority.

#### I have my own infrastructure and community, what's the point of joining the Scala Platform?

Joining the Scala Platform is a declaration of intention. It's about creating a bigger
community that ensures the long-term maintainance of high-quality Scala libraries
for broad and public use. It's about encouraging companies to contribute back to
the software they use in production, and making the process easier for any party involved
in its development.

You should join the Platform if you believe in our ideas and want to see your project
evolving over time. After all, collaborative effort is what moves humanity forward.

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
encouraged to propose the inclusion of those policies in the official Code of
Conduct. We all should define what civic behaviour is.

#### I want to report the violation of the CoC, what shall I do?

Contact the SPP Process Lead ([@jvican](https://github.com/jvican)), or a Scala
Center representative and explain the situation. You can also get in touch with
any Scala Platform Committee member or module maintainers you want.
