# The Drone CI integration

Our servers provide quick development experience to module maintainers.
With a powerful baseline server and dynamic scaling, we ensure concurrent and
fast build times.

> {.warning}
> This is work in progress.

## Setup

1. Go to [our Drone setup](http://stats.lassie.io:8001).
2. Log in with your GitHub credentials and turn on the CI in your module.
3. Push a `drone.yml` file with the build logic and voilà.

> {.warning}
> Only official modules maintainers can log in.

### Creating the `drone.yml` file

The `drone.yml` file is a YAML file that tells Drone how to behave when
GitHub kicks it in. Let's see an example.

```yaml
build:
  image: scalaplatform/scala-sbt-git
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

cache:
  mount:
    - .git
    - /drone/.ivy2
    - /drone/.coursier-cache
    - /drone/.sbt
```

#### The official Scala Platform docker image

Drone is, in a nutshell, a CI wrapper around Docker. This has great benefits:
you don't need to set up your own image, we already provide you one.

Pulling `scalaplatform/scala` gives you a fully-working Docker image with
small size and a common working environment for Scala developers. In addition,
you get a cached environment for free: say goodbye to downloading dependencies
every time you run the CI.

## Common configuration settings

1. Get latest Docker image

By default, the Docker image is not updated to benefit from reproducible build results.
If you want Drone to fetch the latest version every time is executed, add
the following attribute to the `build` section:

```yaml
pull: true
```
