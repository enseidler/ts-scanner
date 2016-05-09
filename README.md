# TSScanner

With ``TSScanner`` you'll be able to analyze and detect certain parts of the "TS" files. From starting by displaying the packages, to asking PAT and PMT tables, or extract the video and audio of each program.

***


### Requirements
- Java 1.5 (or newer)


### Download latest release
[![GitHub release](https://img.shields.io/github/release/enseidler/ts-scanner.svg)](https://github.com/enseidler/ts-scanner/releases/latest)

(You have to choose either files "tar.gz" or "zip" and extract within directory you want)


### Usage (since v1.1.1)
- New easy way to use has been added:
``` shell
 ./tscan <command> <file-path>
```
(Before start to use this way, please give some execute permissions ``chmod +x tscan``)


### Old fashion way
``` shell
  java -jar TSScanner.jar <command> <file-path>
```


### Commands
- **map:** visualization of packages *(since v1.0.0)*
- **pat:** PAT table request *(since v1.1.0)*
- **len:** length of ts file, given by amount of packages *(since v1.1.2)*


### License
Licensed under the [GNU General Public License v3.0](https://github.com/enseidler/ts-scanner/blob/master/LICENSE).
