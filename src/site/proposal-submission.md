# Submit a proposal

The Scala Platform is shaped through Scala Platform proposals, documents
that propose the inclusion of a library into the Platform to fulfill a
concrete need. Proposals explain why such need is desirable and why the
suggested library is the best fit for the Scala community.

If you want to submit a library proposal, follow these steps:

1.  [*Open a Discourse discussion thread*](http://internals.scala-lang.org)
    describing your idea to get feedback from the community. Share this link in
    Gitter channels and with other developers whose feedback you consider
    important.
2.  After two weeks, the Process Lead invites you to submit it, picks a
    reviewer and schedules its discussion for the next available Scala Platform meeting.
3.  The reviewer presents the proposed idea. The Committee votes it.
4.  Committee selects and ranks other potential modules by widespread
    use and approachability. Committee makes sure they all have
    maintainers willing to participate into the SPP process.
5.  Committee incubates the highest-ranked project per category, e.g.
    IO, JSON, property-based testing suite, unit testing
    framework, etc.
6.  Incubation process is useful for letting the committers get used to
    their new responsibilities, chasing and fixing bugs before a
    stable release and attracting more developers to contribute. The
    review is called on by the reviewer and the committers of
    a module.
    
    The incubation process lasts, at maximum, six months. The Committee
    will review an incubated project when both authors and reviewer agree
    that the project is ready. Modules are merged when they:
    *  Have passed their incubation period;
    *  Have at least one committer cutting releases and maintaining them; and,
    *  Abide by the Code of Conduct.
    
7.  If past the incubation time a module doesn't meet the requirements,
    the second highest-ranked project is incubated instead.
8.  Modules are withdrawn from the Platform when they fall unmaintained.

## Benefits of becoming a module

Aside from better visibility in the community, library authors and
maintainers benefit from an array of services provided by the Scala
Center:

1.  Powerful build machines. An alternative to Travis, with more memory
    and CPU resources to ensure a fast development cycle.
2.  Organizational help:

    *  Code of Conduct;
    *  Process to elect committers;
    *  Process to evolve the modules of the platform;
    *  Ticket management, PR & core reviews policies;
    *  Scala documentation website (public scaladoc);
    *  Good-looking website template and infrastructure to write and
        update the docs;
    *  Easy way to to showcase library's use via reproducible code snippets;
        and,
    *  CLA bots.
3.  Automatic release of modules.

    *  Maintainers of module have private access to a server.
    *  The server contains all the infrastructure to cron jobs,
        automate the release of their modules and release daily with
        a bot.
    *  Private access to maintainers’ GPG keys is ensured.
    *  A bot checks binary compatibility with MiMa. Advanced binary
        compatibility checks (added and removed methods) may come in
        the future.
    *  Support for source compatibility might be provided in
        the future.

The above services focus on improving the development experience and
allowing contributors to focus on the remarkable parts of their task.

## What happens after?

After getting the approval from the Committee and passing the incubation
period, the module lifetime begins. It is then merged in the next
release of the Platform.

As this is the hardest phase of a module, the Scala Platform process
provides policies and help managing the project. These policies enforce
the Platform contract and make sure that modules abide by some minor
common rules.

