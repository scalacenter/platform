# FAQ (Frequently Asked Questions)

If you have a question that it's not here, [send a PR](https://github.com/scalaplatform) or ask in [Gitter](https://gitter.im).

## About the process and the Platform

#### How can I get involved?

Here is what you can do:

* Create a Scala Platform proposal;
* Discuss and give feedback on current proposals in [Scala Internals](https://internals.scala-lang.org);
* Help contributing and get the infrastructure right ([`sbt-platform`](https://github.com/jvican/platform-staging)).
* Contribute to any Scala Platform module by:
  * Adding features;
  * Fixing bugs; or
  * Improving documentation.
  
Choose one or more and help us improve the Scala ecosystem!

#### Who can submit a Scala Platform proposal?

*Anyone*. It doesn't matter if you are a Committee member, a library author or an open-source Scala developer.
Go ahead and [follow the process](proposal-submission.md).

#### Is submitting a Scala Platform proposal easy?

The Scala Platform process is designed to be simple and lightweight, significantly more than
the [Scala Improvement Process](http://docs.scala-lang.org/sips/sip-submission.html).
The Scala Platform Committee quickly reviews proposals and gives feedback to authors,
and turnaround times are tweaked to make progress in a short period of time. However,
it's worth noting that you need to be committed to devote some time into its development
in case the Committee accepts your proposal. Everyone involved in the process will help
you make everything as smooth as possible, and help you in this interesting journey with us.

In a nutshell, the [phases of the submission](proposal-submission.md) are:

1. Get early feedback from the Community. Look for supporters.
1. Participate in the discussions. Let the Committee discuss your idea.
   The Committee, with input from the Community, decides which library gets incubated.
1. Incubate the library and prepare it for a stable release.

If you wish to know more, check [the submission steps](proposal-submission.md).

#### Why is it called the **Scala** Platform?

The Scala Platform process is designed by [the Scala Center](https://scala.epfl.ch/), an EPFL
non-profit organization that works for the good of the Scala programming language and its community.

As an official initiative by EPFL, the university that gave birth and holds the trademark of Scala,
the Platform has been baptised as the *Scala Platform* because it aims at:

* Helping developers learn and become successful with the language;
* Providing stable libraries that inustrial users can depend on;
* Grow the Scala community and help its expansion; and,
* Increasing the impact of the Scala ecosystem.

#### Is this the only Platform for Scala?

No, not at all! The split of the standard library into core and platform is to
empower Community members to create their own platform. Given the vast diversity
that the Scala community has, everyone can benefit from creating their own
platform with their unique features.

## Infrastructure

#### My module already has a release process, can I keep it?

You can keep it, and as a maintainer you can even release versions whenever you
want, but you will need to modify your release process to include the release
rules specified in the Scala Platform process.

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

#### My module has its own CI, can I keep using it?

Sure, you can keep it so long as it fulfills the same purposes. The important thing is
not what infrastructure or CI you use, but the fact that you use it. Setting up
our CI gives you some performance benefits and automatic release process,
though it's up to you if you need them or not.

#### Is the release bot using Platform's Sonatype organization for the releases?

You choose whether we do or not. Check the [`sbt-platform` docs](#maven-coordinate).

#### I want to use Sonatype, what can I do?

Support for Sonatype is not provided in `sbt-platform` for now. However, you can release to
Bintray and then synchronize your artifact with Maven Central. If you have rights to publish
under the `groupId` of the artifact, then open sbt. When you're sure that the CI has published
the stable version in Bintray, execute the `syncMavenCentral` task and introduce your credentials.

Within two hours, you should see your artifact hitting the doors of Maven Central.

#### Do I have to move my GitHub project into an independent organization?

In principle, you don't. Your user will be added to the [Scala Platform GitHub organization](https://www.google.com/url?q=https%3A%2F%2Fgithub.com%2Fscalaplatform&sa=D&sntz=1&usg=AFQjCNFStwF1ZVQ_M25UWiiNvIcRTOIdUw),
but your module can stay in your domain. The idea is that this rule, and others,
will be discussed along with module maintainers when modules start to join the Platform.

Whether or not you will need to move your module is a decision that will be taken
by the whole Scala Community.

#### I have my own infrastructure and community, what's the point of joining the Scala Platform?

Joining the Scala Platform is a declaration of intention. It's about creating a bigger
community that ensures the long-term maintenance of high-quality Scala libraries
for broad and public use. It's about encouraging companies to contribute back to
the software they use in production, and making the process easier for any party involved
in its development.

You should join the Platform if you believe in these ideas and want to see your open-source
project evolving over time, regardless of the help that the Scala Center provides. We all
benefit from big, collaborative and inclusive projects.

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
Center representative and explain the situation. You can also get inform of the
situation to any moderator in the [Scala Discourse](https://dev.scala-lang.org) channel.
