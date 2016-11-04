# The Drone CI integration

> {.warning}
> This is work in progress.

Our servers provide quick development experience to module maintainers.
With a powerful baseline server and dynamic scaling, we ensure concurrent and
fast build times.

## Setup

1. Go to [our Drone setup](http://stats.lassie.io:8001).
2. Log in with your GitHub credentials and turn on the CI for your module.
3. Push a `drone.yml` file with the build logic.
4. See how the CI executes in the Drone project view.

> {.note}
> Access to CI is restricted to official module maintainers.
> The SPP Process Lead will give access to them when the module
> joins the Platform or they are elected.

### Creating the `drone.yml` file

The `drone.yml` file is a YAML file that tells Drone how to behave when
GitHub kicks it in. Let's see an example.

```yaml
pipeline:
  build:
    image: scalaplatform/scala
    volumes:
      - /platform:/keys
    environment:
      - GITHUB_PLATFORM_TOKEN=$$GITHUB_PLATFORM_TEST_TOKEN
      - PLATFORM_PGP_PASSPHRASE=$$PLATFORM_PGP_PASSPHRASE
      - BINTRAY_USERNAME=$$BINTRAY_USERNAME
      - BINTRAY_PASSWORD=$$BINTRAY_PASSWORD
    commands:
      - sbt clean test sbt-platform/clean sbt-platform/test sbt-platform/publishLocal sbt-platform/scripted
      - sbt process/publishProcessAndDocs
    notify:
      # Gitter and email will be supported in the future
      image: slack 
      when:
        status: [ failure ]

cache:
  mount:
    - .git
    - /drone/.ivy2
    - /drone/.coursier-cache
    - /drone/.sbt
```

#### The official Scala Platform docker image

Pulling `scalaplatform/scala` gives you a fully-working Docker image with
small size and a common working environment for Scala developers. In addition,
you get a cached environment for free: say goodbye to downloading dependencies
every time you run the CI.

## Common configuration settings

### Add your special tokens to the image

Drone secrets allow you to store sensitive information like passwords and tokens.

### Get latest Docker image

By default, the Docker image is not updated to benefit from reproducible build results.
If you want Drone to fetch the latest version every time is executed, add
the following attribute to the `build` section:

```yaml
pull: true
```

### Test different Scala versions

Drone provides a way with [matrix builds](http://readme.drone.io/0.5/usage/matrix/).
However, we don't explain how this feature works because sbt already takes care of it.

You can achieve the same behaviour by prepending `+` to `compile` or `test`.
SBT then executes the tasks for the Scala versions configured in `crossScalaVersions`.
`sbt-platform` ensures this value contains the latest two Scala versions.

### Failure notifications



