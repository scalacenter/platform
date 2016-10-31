# FAQ (Frequent Asked Questions)

If you have a question that it's not here, [send a PR](https://github.com/scalaplatform) or ask in [Gitter](https://gitter.im).

## About the process and the Platform

#### How can I get involved?

Here is what you can do:

* Create a Scala Platform proposal;
* Discuss and give feedback on current proposals in [Scala Internals](https://internals.scala-lang.org);
* Contribute to any Platform module by:
  * Adding features;
  * Fixing bugs; or
  * Improving documentation.
  
Choose one or more and help us improve the Scala ecosystem!

#### Who can submit a Scala Platform proposal?

*Anyone*. It doesn't matter if you are a Committee member, a library author or an open-source Scala developer.
Just [follow the process](proposal-submission.md).

#### I don't like wasting my time, is submitting a Scala Platform proposal easy?

The Scala Platform process is designed to be simple and lightweight, even more than
the [Scala Improvement Process](http://docs.scala-lang.org/sips/sip-submission.html).
The Scala Platform Committee quickly reviews proposals and gives feedback to authors,
and turnaround times are tweaked to make progress in a short period of time. If you
wish to know more, check [the submission steps](proposal-submission.md).

#### Why is it called the **Scala** Platform?

The Scala Platform process is designed by [the Scala Center](https://scala.epfl.ch/), an EPFL
non-profit organization that works for the good of the Scala programming language and its community.

As an official initiative by EPFL, the university that gave birth and holds the trademark of Scala,
the Platform has been baptised as the *Scala Platform* because it aims at:

* Helping developers learn and become successful with the language;
* Providing stable libraries that industrial users can depend on; and,
* Increasing the impact of Scala and its community.

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

The Scala Platform process ensures reasonable rules to keep both maintainers and contributors happy.
Having a default release process gives predictability to the contribution process and encourages
companies to assign resources and engineers. Remember how motivated we are when our patches get
merged, or how easy is for companies to assign an engineer to fix bugs if you guarantee they can depend on a new release tomorrow.

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

You should join the Platform if you believe in these ideas and want to see your open-source
project evolving over time, regardless of the help that the Scala Center provides. We all
benefit from collaborative projects.

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
