# The Drone CI integration

Our servers provide quick development experience to module maintainers.
With a powerful baseline server and dynamic scaling, we ensure *concurrent and
fast build times*.

## What does Drone provide you?

Drone is a Continuous Integration platform built on container technology. Every build
is executed inside an ephemeral Docker container, giving developers complete control over
their build environment with guaranteed isolation.

Drone stands out because it provides:

* A generic and configurable build for all the projects.
* Control over the secrets exposed to your project under different situations.
* Security over the images that can access secrets (tokens, private keys).
* Command-line interface to quickly manage the CI from your console.
* A way to run concurrent builds (for non-sequential tasks).
* Constraints on your build tasks. Start a build task only when:
    * A concrete branch(es) has a new event;
    * A push is performed;
    * A pull request is submitted;
    * A git tag is pushed;
    * A deploy event happens.
    
* Notifications when a change of status happens, e.g. send
notifications via email/Slack/Gitter when the build succeeds or fails.

* Control over the platform that executes the build (Linux, Windows).

* Access to environment variables to programmatically create your sbt tasks.

## Setup

1. Join the `scalaplatform` GitHub organization if you're a module maintainer but not a member.
1. Go to our Drone setup: [https://platform-ci.scala-lang.org](https://platform-ci.scala-lang.org).
1. Log in with your GitHub credentials and turn on the CI for your module.
1. Create a `drone.yml` file with the build logic. Sign it. Push the changes.
1. Observe the logs and result of the build in the CI.

> {.note}
> Access to CI is restricted to official module maintainers.
> If you want to have access to the CI, you need to join the `scalaplatform` GitHub organization.

### Installing the Drone CLI

The command-line Drone interface allows you to configure and inspect the state of
our Drone servers. However, it also allows you to execute tasks that are inaccessible
from the web interface, like signing the build files, executing Drone locally and managing secrets.

If you're an official module maintainer, you will certainly need it.

Learn how to install it in the [official docs][drone].

#### Signing your `.drone.yml` after changing it
```bash
drone sign your-github-id/your-repo
```

#### Executing drone locally

```bash
drone exec --local --privileged your-github-id/your-repo
```

#### Secrets

See [the Secrets section](#secrets).

### Creating the `drone.yml` file

The `drone.yml` file is a YAML file that tells Drone how to behave when
GitHub executes webhooks.

To get you up and running, use the following skeleton:

```yaml
pipeline:
  # Fetch folders from distributed cache
  sftp_cache_restore:
    image: plugins/sftp-cache
    restore: true
    mount:
      - /drone/.ivy2
      - /drone/.coursier-cache
      - /drone/.sbt
      - /drone/.git

  # Build your project, executes tests, releases docs...
  build:
    image: scalaplatform/scala:0.5
    pull: true
    volumes:
      - /platform:/keys
    commands:
      - ${COMMAND #1}
      - ${COMMAND #2}
      
  # Release stable whenever a tag is pushed to `platform-release`
  release-stable:
    image: scalaplatform/scala:0.5
    commands:
      - sbt releaseStable
    when:
      branch: platform-release
      event: tag

  # Save folders in distributed cache
  sftp_cache_rebuild:
    image: plugins/sftp-cache
    rebuild: true
    mount:
      - /drone/.ivy2
      - /drone/.coursier-cache
      - /drone/.sbt
      - /drone/.git
```

Make sure you replace the `commands` key in the `build` section
with your build steps (e.g. `sbt clean test docs/publish`).

If you want to learn customize the build, keep on reading.

> {.warning}
> Every time you modify `.drone.yml`, you need to sign the configuration file.
> Signing is necessary for security purposes: it prevents you from malicious
> users that want to hijack your setup and stole your keys.

#### The official Scala Platform docker image

Docker enables us to bundle an official Scala Platform image (`scalaplatform/scala`)
with popular Scala tools, predefined configurations and private keys for the release.
The benefits of this image are two-fold: it provides a plug-and-play experience while
still being lightweight and portable.

If you want to use it locally, run `docker pull scalaplatform/scala` followed by
`docker run -i -t scalaplatform/scala bash`. For the CI, make sure that the build
section of `.drone.yml` is `scalaplatform/scala`.

## Common configuration settings

Drone configuration files are built around the concept of sections.

All the tasks that you may want to define go under the global `pipeline`
section. In the previous example, four independent tasks were defined:
`sftp-cache-restore`, `build`, `release-stable` and `sftp-cache-rebuild`.

> {.note}
> Note that tasks are executed sequentially in the same way they are defined.
> The names of the subtasks are not important and must not be unique.

### Adding a new independent subsection

Add a subsection under `pipeline` that defines, at least, the image and commands
to be executed:

```yaml
pipeline:
  ...
  mynewtask:
    image: redis
    commands:
      - redis-server
  ...
```

Drone will pick it up and execute all the sub tasks in the order they are defined.

### Need a command in the Docker image?

* Create a PR to install a new package in the [Dockerfile](https://github.com/jvican).
* Contact the [SPP Process Lead](https://github.com/jvican) in Gitter and ask him to do it. 

> {.note}
> Read [Get latest Docker image](#latest-docker-image) if Drone keeps failing
> to find the binary in the Docker image.

### Add your special tokens to the image {#secrets}

Drone secrets allow you to store sensitive information like passwords and tokens.
Think of the GitHub, Bintray and Sonatype tokens your sbt infrastructure may need.

Secrets are scoped to a repository, and are only visible if `.drone.yml` has been signed.
They are mapped to environment variables in the Docker images.

To add a secret, use the Drone CLI:

```bash
drone secrets add your-user/your-github-repo KEY VALUE
```

where `your-user/your-github-repo` is your GitHub handle, `KEY` is the name of the
environment variable, and `VALUE` is the value associated with it.

If you only want to expose the secrets for a concrete Docker image, use

```bash
drone secrets add --image=scalaplatform/scala your-user/your-github-repo KEY VALUE
```

From then on, the secret `KEY` will be available as an environment variable.
`sys.env.get("KEY")` will give you `VALUE`.

> {.note}
> Remember that secrets are only exposed for signed images. Only module maintainers
> can sign the images.

If you're stuck or want to know more about secret management, check the
[official documentation](http://readme.drone.io/usage/secret-guide/).

### Automatically update the Docker image
<a name="latest-docker-image"></a>

By default, the Docker image is not updated because reproducibility of the CI is key,
and further updates could break your build. To enable this feature,
add the following attribute to the `build` section:

```yaml
pull: true
```

### Test different Scala versions

Drone provides a way with [matrix builds](http://readme.drone.io/usage/matrix-guide/).

However, this feature doesn't provide you much value if you're using sbt.
Prepend `+` to `compile` or `test` and run the task. Sbt will execute the prepended tasks for all
the Scala versions defined in the sbt setting `crossScalaVersions`.

By default, the `sbt-platform` plugin ensures this value contains the latest two Scala versions.

### Get success and failure notifications

Add the following to your `.drone.yml` file:

```yaml
pipeline:
  ...
  # your tasks
  ...
  notify:
    image: drillster/drone-email
    host: smtp.mailgun.org
    username: noreply@drone.geirsson.com
    password: ${MAILGUN_PASSWORD} # requires setup by admin, please contact us on gitter.
    from: noreply@drone.geirsson.com
    recipients:
      - your@email.here
    when:
      event: push # only run on merge into master
      branch: [master]
      status: [changed, failure]

```

For more information on notifications and templates, check the [official Drone documentation][drone].

## Troubleshooting (FAQ)

### I got the error `dial tcp: missing address`.
Your repo is missing the secrets to use sftp-cache plugin. Please contact us on [gitter](https://gitter.im/scalacenter/platform-staging)

## Want to know more?

This page only explains the common use cases when using Drone. For fancier configuration,
we encourage module maintainers to check the [official Drone documentation][drone].

> {.warning}
> Drone 0.5 is still beta, so documentation is not as complete as one would expect. In the following
> weeks, the documentation of the Drone integration as well as the official Drone docs will improve,
> as features stabilize and spurious bugs disappear.

[drone]: http://readme.drone.io/
