# Why become a module?

The focus of the Scala Platform process is on *growing communities
around the Platform modules*. For that, it enforces commonly-approved
open-source rules that optimize the experience of contributors and
maintainers, and provides infrastructure that makes easier developers'
life.

1.  Powerful build machines, with large memory and powerful CPU resources,
    that ensure a fast development cycle.
    
2.  Organizational help.

    *  [Scala Center Code of Conduct](https://docs.google.com/document/d/1B57XIj2zIh7xx1syKvS3qfC4L8usd0pI0yTSrJMfuew/edit#);
    *  Process to elect maintainers, committers and evolve the modules;
    *  Policies on ticket management, PR & core reviews policies;
    *  Scala documentation website (public scaladoc);
    *  Good-looking website under an official Scala namespace;
    *  Template and infrastructure to write and update the docs; and
    *  Integration with online Scala tools to provide reproducible code snippets.
    
3.  Automatic release of modules.

    *  Maintainers of module have access to servers and CI.
    *  A bot automatically releases modules every day, except for
       stable releases (module maintainers must release with their
       private organization credentials).
    *  The automatic release process checks binary compatibility in every
       release, and warns module maintainers when releases break our [stability
       guarantees](policies.md#release).
       > {.warning}
       > Advanced binary (added and removed methods) and source-compatible
       > checks may be provided in the future.
       
> {.note}
> Any question so far? check the [FAQ](faq.md).

The above services focus on improving the development experience,
removing the overhead of getting the contribution rules and the infrastructure
right.
