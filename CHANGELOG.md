# Changes

#### [Version 0.1.1]

- Updated to the latest Stargate API
- Converted to Gradle and updated minimum requirements to Paper 1.20
- Fixed several behavioural and stability problems involving the `G` flag.
- Finalised `G` gate coordinate parsing format; merged proposed`J` gate behaviour into this flag.
- Fixed a load of small problems involving coordinate parsing.
- Added the capability to process overlength strings through the flags line via a GUI.
- Prevent an exception resulting from direct removal of behaviour flags.
- Resolved several persistence issues.
- Implemented the `D` flag.
- Overhauled the configuration file and completed missing implementations.
- Added some missing feedback messages.
- Fixed several bugs related to `E` flags.
- Added `GA` functionality.
- Prevented the concurrent presence of multiple behaviour flags on a single gate.
- Fixed some problems involving unexpected `H` and `S` flag behaviour.
- Fixed a crash involving the `V` flag.
- Fixed some message substitution and localisation problems.
- Improved sign text serialization.
- Added to the main stargate build pipeline.

#### [Version 0.1.0]

- Packaged the below 0.X changes for release.
- Changed some documentation and CI stuffs.

#### [Version 0.0.5]

- Added the `G` flag

#### [Version 0.0.4]

- Added the `E` flag
- Moved the `H`, `N`, `Q`, and `S` flags from the core.

#### [Version 0.0.3]

- Updated to new version of SGR's API
- Added the `D` flag
- Added the `V` flag

#### [Version 0.0.2]

- Added `config.yml`

#### [Version 0.0.1]

- Initial release
- Documentation moved to [here](https://sgrewritten.org/infosgm).
- Updated to use the new SGR [API](https://sgrewritten.org/api).
- Now requires at least Stargate v1.0.0.15
