# Platform policies

The Platform process establishes some policies to deal with the organization
of the modules.

These policies have a major goal: they enable companies and industrial
users to contribute back to the modules, and help into its maintenance.
In exchange, the Platform provides serious stability guarantees to users and
basic building blocks to evolve friendly communities around the modules.

These building blocks consist of a predictable development workflow and
clear processes to maintain and contribute to the modules. Altogether, they
help maintainers and contributors, and encourage users to depend on the Platform in
production. The seamless interaction between these three parties is key for
the success of the Scala Platform.

## Policies on release and stability
<a name="release"></a>

1.  The Platform is a published artifact of all the selected libraries that meet
    the compatibility criteria.
2.  The Platform follows a **twelve-week** (3 months) release train model.
    > {.note}
    > A release train model consists in a regular pre-planned schedule of software
    > releases. This release model was introduced by the browsers to achieve stability
    > without stagnation. When merged changes hit master, nightly releases are published
    > for developers. After six months, a beta release is published from the state of
    > master, and it’s promoted to become the stable release. 


    1.  The branch `platform-release` is used to cut releases.
    2.  Modules must use semantic versioning.
    3.  Minor versions are released every 12 weeks.
    4.  Modules can only break source and backwards compatibility in
        major releases, that happen: (a) every 18 months (1 year and 6 months),
        or (b) when new Scala versions are released.
    5.  Unmaintained libraries are removed only in major releases.
    6.  Nightly versions of the modules are released every night (if changes).
    7.  Every twelve weeks, the latest nightly is promoted to the beta
        version and the latest beta to stable.

        ```
        Latest nightly *1.0.0-NIGHTLY-12-03-2016* becomes *1.0.0-b2*.
        Latest beta *1.0.0-b1* becomes the stable *1.0.0*.
        ```

    8.  If bugs are found in beta versions, maintainers can provide exceptional
        artifacts before that beta is promoted to stable.
    9.  Modules with no changes in `platform-release` are not
        released again. Previous versions are used.
    10.  Exceptional patch releases may be cut if important bugs are
        found in stable versions.
    11.  Bug fixes are accepted and released for the major Platform
        releases in the last 24 months.
3.  The Platform must be cross-compiled with the latest two major
    Scala releases. *E.g.* If 2.12 is the current Scala version, 2.12
    and 2.11.
4.  Four different release pipelines:

    *  **Nightly** releases are used for early adopters and developers.
    *  **Beta** releases are used for potential users that want to test changes
        and behaviour.
    *  **Stable** releases are ready for production use.
    *  **Bug fixes for stable versions**. Necessary when critical bugs
        are found. Some fixes may break binary and
        source compatibility. The release of such fixes is up to
        modules’ maintainers.
5.  Public methods marked experimental belong to the Experimental API.
    They do not ensure backwards compatibility and may be removed
    without further notice.
6.  *MiMa* ensures binary compatibility. Maintainers must take care that
    changes forwarded to the branch *platform-release* meet the
    compatibility criteria.

## Policies on committers

1.  Authors of Scala Platform proposals become committers.
2.  Committers are contributors with write-access to a module repository.
3.  Future committers are selected by current committers on a vote.
4.  Committers are contributors to a module and participate into the development
    of the module from time to time.
5.  Committers are charged with the short-term evolution of a module.
6.  All the committers need to abide by the [*Scala Center Code of
    Conduct*](https://docs.google.com/document/d/1B57XIj2zIh7xx1syKvS3qfC4L8usd0pI0yTSrJMfuew/edit#)
    and follow the release process.

## Policies on contributors

1.  The platform is maintained and developed by the community, therefore
    contributions from Scala developers are encouraged!
2.  Committers agree to review and, if appropriate, incorporate contributions in
    a timely fashion.
3.  Contributors are eligible to become committers (on a vote) after three
    months of work and involvement in the module.
