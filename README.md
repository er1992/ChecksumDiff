**ChecksumDiff** is an application that allows for comparison of the content of two revisions of a folder and generating the difference between the two revisions. **ChecksumDiff** generates the JSON view of the two folders recursively and compares the generate JSON files. It then picks the a source folder and generates a third folder, only containing the changes between the two compared folders and pulls in the files from the source to the generated folder.

**Use case**: An application is deployed locally. There is a need to upgrade this application. A build is from the latest source code is generated locally and needs to be transfered to the remote location. Given the low upstream network bandwidth, this process could takes many many hours depending on the size of the build. By generating a JSON view of the remote deployed folder and comparing it with the local generated version, **ChecksumDiff** generates a lean deploy folder containing only the files that have changes between the two revisions and also any new files added. This could save many wasted hours and MBs.

Features coming soon:

- Multithreading (The app uses less than 10-15% disk usage on a standard HDD, lots of room for improvements)
- App Properties file for consistent properties to be shared across a team
- Options: 
  * Output the JSONs to files or only keep in memory and use interally
  * Apply File Exmeption list
