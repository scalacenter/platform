# Platform policies

The Platform process establishes some policies to deal with the governance and
organization of the modules.

These policies have a major goal: they enable companies and industrial
users to contribute back to the modules, and help into its maintenance.
In exchange, the Platform provides serious stability guarantees to users and
basic building blocks to evolve friendly communities in the Scala ecosystem.

These building blocks are two: a predictable development workflow and a
clear contract to maintain and contribute to the modules. Altogether, they
help maintainers and contributors, and encourage users to depend on the Platform
in production systems.

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
6.  *MiMa* ensures binary compatibility. However, maintainers shall take
    care that changes forwarded to the branch *platform-release* meet the
    compatibility criteria.

### Decide the Maven coordinates
    
When releasing stable versions, module maintainers decide under which group id
the official artifacts get released. If you want to release under your own name,
you need to get access to 

## Policies on committers and contributors

The Platform process uses a modified version of C4, a contract designed for
open-source projects and communities by [Pieter Hintjens](https://en.wikipedia.org/wiki/Pieter_Hintjens).
The modifications affect licensing and adds specific Scala Platform requirements.

Being used at successful communities like ZeroMQ, C4 has proven its value
when building thriving open-source communities capable of producing high-quality
software. In the best spirit of the Scala Platform process, the following contract
hopes to reproduce their success in the Scala community.

> The Collective Code Construction Contract (C4) is an evolution of the github.com [Fork + Pull Model](https://help.github.com/articles/about-pull-requests/), aimed at providing an optimal collaboration model for open-source software projects. This is revision 3 of the C4 specification, tailored specifically for the Scala Platform process.
>  
>   ### License
>  
>  Copyright (c) 2016-2017 Jorge Vicente Cantero.
>
>  Copyright (c) 2009-2016 Pieter Hintjens.
>  
>  This Specification is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
>  
>  This Specification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
>  
>  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
>  
>  ### Abstract
>  
>  C4 provides a standard process for contributing, evaluating and discussing improvements on software projects. It defines specific technical requirements for projects like a style guide, unit tests, `git` and similar platforms. It also establishes different personas for projects, with clear and distinct duties. C4 specifies a process for documenting and discussing issues including seeking consensus and clear descriptions, use of "pull requests" and systematic reviews.
>  
>  ### Language
>  
>  The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in [RFC 2119](http://tools.ietf.org/html/rfc2119).
>  
>  ### 1. Goals
>  
>  C4 is meant to provide a reusable optimal collaboration model for open source software projects. It has these specific goals:
>  
>  1. To maximize the scale and diversity of the community around a project, by reducing the friction for new Contributors and creating a scaled participation model with strong positive feedbacks;
>  1. To relieve dependencies on key individuals by separating different skill sets so that there is a larger pool of competence in any required domain;
>  1. To allow the project to develop faster and more accurately, by increasing the diversity of the decision making process;
>  1. To support the natural life cycle of project versions from experimental through to stable, by allowing safe experimentation, rapid failure, and isolation of stable code;
>  1. To reduce the internal complexity of project repositories, thus making it easier for Contributors to participate and reducing the scope for error;
>  1. To enforce collective ownership of the project, which increases economic incentive to Contributors and reduces the risk of hijack by hostile entities.
>  
>  ### 2. Design
>  
>  #### 2.1. Preliminaries
>  
>  1. The project SHALL use the git distributed revision control system.
>  1. The project SHALL be hosted on github.com or equivalent, herein called the "Platform Host".
>  1. The project SHALL use the Platform Host issue tracker.
>  1. The project SHOULD have clearly documented guidelines for code style.
>  1. A "Contributor" is a person who wishes to provide a patch, being a set of commits that solve some clearly identified problem.
>  1. A "Maintainer" is a person who merges patches to the project. Maintainers are not developers; their job is to enforce process.
>  1. Contributors SHALL NOT have commit access to the repository unless they are also Maintainers.
>  1. Maintainers SHALL have commit access to the repository.
>  1. Everyone, without distinction or discrimination, SHALL have an equal right to become a Contributor under the terms of this contract.
>  
>  #### 2.2. Licensing and Ownership
>  
>  1. A project that joins the Scala Platform SHOULD choose a license. Share-alike licenses such as the MPLv2 or LGPL are RECOMMENDED.
>  1. All contributions to the project source code ("patches") SHALL use the same license as the project.
>  1. All patches are owned by their authors. There SHALL NOT be any copyright assignment process.
>  1. Each Contributor SHALL be responsible for identifying themselves in the project Contributor list.
>  
>  #### 2.3. Patch Requirements
>  
>  1. Maintainers and Contributors MUST have a Platform Host account and SHOULD use their real names or a well-known alias.
>  1. A patch SHOULD be a minimal and accurate answer to exactly one identified and agreed problem.
>  1. A patch MUST adhere to the code style guidelines of the project if these are defined.
>  1. A patch MUST adhere to the "Evolution of Public Contracts" guidelines defined below.
>  1. A patch SHALL NOT include non-trivial code from other projects unless the Contributor is the original author of that code.
>  1. A patch MUST compile cleanly and pass project self-tests on at least the principle target platform.
>  1. A patch commit message MUST contain information about the problem that is solving and how.
>  1. A "Correct Patch" is one that satisfies the above requirements.
>  
>  #### 2.4. Development Process
>  
>  1. Developers involved in the evolution of the project MUST abide by the [*Scala Center Code of Conduct*](https://docs.google.com/document/d/1B57XIj2zIh7xx1syKvS3qfC4L8usd0pI0yTSrJMfuew/edit#).
>  1. Change on the project SHALL be governed by the pattern of accurately identifying problems and applying minimal, accurate solutions to these problems.
>  1. To request changes, a user SHOULD log an issue on the project Platform Host issue tracker.
>  1. The user or Contributor SHOULD write the issue by describing the problem they face or observe.
>  1. The user or Contributor SHOULD seek consensus on the accuracy of their observation, and the value of solving the problem.
>  1. Users SHALL NOT log feature requests, ideas, suggestions, or any solutions to problems that are not explicitly documented and provable.
>  1. Thus, the release history of the project SHALL be a list of meaningful issues logged and solved.
>  1. To work on an issue, a Contributor SHALL fork the project repository and then work on their forked repository.
>  1. To submit a patch, a Contributor SHALL create a Platform Host pull request back to the project.
>  1. A Contributor SHALL NOT commit changes directly to the project.
>  1. If the Platform Host implements pull requests as issues, a Contributor MAY directly send a pull request without logging a separate issue.
>  1. To discuss a patch, people MAY comment on the Platform Host pull request, on the commit, or elsewhere.
>  1. To accept or reject a patch, a Maintainer SHALL use the Platform Host interface.
>  1. Maintainers SHOULD NOT merge their own patches except in exceptional cases, such as non-responsiveness from other Maintainers for an extended period (more than 1-2 days).
>  1. Maintainers SHALL NOT make value judgments on correct patches.
>  1. Maintainers SHALL merge correct patches from other Contributors rapidly.
>  1. Maintainers MAY merge incorrect patches from other Contributors with the goals of (a) ending fruitless discussions, (b) capturing toxic patches in the historical record, (c) engaging with the Contributor on improving their patch quality.
>  1. The user who created an issue SHOULD close the issue after checking the patch is successful.
>  1. Any Contributor who has value judgments on a patch SHOULD express these via their own patches.
>  1. Maintainers SHOULD close user issues that are left open without action for an uncomfortable period of time.
>  1. Any official release of the project must abide by the [Scala Platform release process](#release).
>  
>  #### 2.5. Branches and Releases
>  
>  1. The project SHALL have one branch ("master") that always holds the latest in-progress version and SHOULD always build.
>  1. The project SHALL have one branch ("platform-release") that always holds the latest compatible in-progress changes and SHOULD always build.
>  1. Topic branches are not ENCOURAGED. Personal forks MAY use topic branches.
>  1. To make a stable release a Maintainer shall tag the repository. Stable releases SHALL always be released from the repository master.
>  
>  #### 2.6. Evolution of Public Contracts
>  
>  1. All Public Contracts (APIs or protocols) SHALL be documented.
>  1. All Public Contracts SHOULD have space for extensibility and experimentation.
>  1. A patch that modifies a stable Public Contract SHOULD not break existing applications unless there is overriding consensus on the value of doing this.
>  1. A patch that introduces new features SHOULD do so using new names (a new contract).
>  1. New contracts SHOULD be marked as "draft" until they are stable and used by real users.
>  1. Old contracts SHOULD be deprecated in a systematic fashion by marking them as "deprecated" and replacing them with new contracts as needed.
>  1. When sufficient time has passed, old deprecated contracts SHOULD be removed.
>  1. Old names SHALL NOT be reused by new contracts.
>  
>  #### 2.7. Project Administration
>  
>  1. The project founders SHALL act as Administrators to manage the set of project Maintainers.
>  1. The Administrators SHALL ensure their own succession over time by promoting the most effective Maintainers.
>  1. A new Contributor who makes correct patches, who clearly understands the project goals, and the process SHOULD be invited to become a Maintainer.
>  1. Administrators SHOULD remove Maintainers who are inactive for an extended period of time, or who repeatedly fail to apply this process accurately.
>  1. Administrators SHOULD block or ban "bad actors" who cause stress and pain to others in the project. This should be done after public discussion, with a chance for all parties to speak. A bad actor is someone who repeatedly ignores the rules and culture of the project, who is needlessly argumentative or hostile, or who is offensive, and who is unable to self-correct their behavior when asked to do so by others.
>  
>  ### Further Reading
>  
>  * [Argyris' Models 1 and 2](http://en.wikipedia.org/wiki/Chris_Argyris) - the goals of C4 are consistent with Argyris' Model 2.
>  
>  * [Toyota Kata](http://en.wikipedia.org/wiki/Toyota_Kata) - covering the Improvement Kata (fixing problems one at a time) and the Coaching Kata (helping others to learn the Improvement Kata).
>  
>  ### Implementations
>  
>  * The Scala Platform process uses the C4 process for all the modules of the Scala Platform.
>  * The [ZeroMQ community](http://zeromq.org) uses the C4 process for many projects.
>  * [OSSEC](http://www.ossec.net/) [uses the C4 process](https://ossec-docs.readthedocs.org/en/latest/development/oRFC/orfc-1.html).
>  * The [Machinekit](http://www.machinekit.io/) community [uses the C4 process](http://www.machinekit.io/about/).

