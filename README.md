# Soccer Tournament

Soccer Tournament is a Java command line application using Spring Boot, Spring Shell, and Spring Data.

## Description

Demonstration project to model a soccer tournament with teams, players, coaches, games, and report for the tournament.  

The project fills the following functional requirements:
* Ability to create a tournament
* Ability to add a team to the tournament
* Ability to add a coach to a team
* Ability to add a player to a team
* Ability to create a game between 2 teams
* Ability to enter the result of a game
* Ability to report results from a tournament

The project also fills the following non-functional requirements:
* Written in Java with Maven
* Command line application (no GUI required)
* Persist any information using any method
* Provide unit tests using JUnit

The project has a command line interpreter (CLI) as an interface following a REPL design (Read, Eval, Print Loop).  While the interpreter loop is running, an in-memory database will persist the data entered by the user.

## Getting Started

### Dependencies

* [JDK 1.8 or later](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Maven 3.2+](https://maven.apache.org/download.cgi)

### Installing

Clone the repo:
```
git clone https://github.com/collene/soccer.git
```

### Executing program
```
mvn spring-boot:run -DskipTests
```
The first time the application executes, it will download any dependencies using Maven, and so may take some time to initialize.  Subsequent executions should be much faster.

### Using the CLI

When the application first starts, you'll see the CLI with a prompt:
```
soccer:>
```
Type help to see a list of the commands that are available:
```
soccer:>help
```
To see details about a specific command, use help and the name of the command:
```
soccer:>help create-tournament
```
Once you have finished using the application, type exit to return back to the command line:
```
soccer:>exit
```
Note that the command line interpreter expects parameters to be separated by spaces.  If one of your parameters contains spaces (such as a name), you'll need to use single or double quotes around the parameter.  For example:
```
soccer:>create-tournament 'My Tournament'
```
You can also use the tab key to auto complete commands and the up/down keys to load a command from your history.

See more about Spring Shell [here](https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/), especially when [dealing with spaces](https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/#quotes-handling)
#### Full List of Commands
Built-In Commands:
* clear: Clear the shell screen.
* exit, quit: Exit the shell.
* help: Display help about available commands.
* history: Display or save the history of previously run commands
* script: Read and execute commands from a file.
* stacktrace: Display the full stacktrace of the last error.

Person Commands:
* create-person: Create new person with specified name.

Team Commands:
* add-coach-to-team: Add coach to team.
* add-player-to-team: Add player to team.
* create-team: Create new team with specified name.

Tournament Commands:
* add-game-to-tournament: Add game between two teams to tournament.
* add-team-to-tournament: Add team to tournament.
* create-tournament: Create new tournament with specified name.
* report-tournament-results: Report game results for tournament.
* score-game-in-tournament: Score game between two teams in tournament.

### Executing tests
```
mvn test
```

## Authors

Collene Hansen 

Visit my blog: [https://uncommentedout.com](https://uncommentedout.com)

## Acknowledgments

Inspiration, code snippets, etc:

* [Spring Shell 2.0](https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/)
* [Writing Assertions with Hamcrest](https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-writing-assertions-with-hamcrest/)
* [Make a readme](https://www.makeareadme.com/)
* [Readme template](https://gist.github.com/DomPizzie/7a5ff55ffa9081f2de27c315f5018afc)
* [Spring Shell TableBuilder examples](https://www.programcreek.com/java-api-examples/?api=org.springframework.shell.table.TableBuilder)
* [Sorting Streams in Java](https://howtodoinjava.com/java8/stream-sorting/)
* [Java Stream Filter with Lambda](https://www.baeldung.com/java-stream-filter-lambda)
* [Persisting Maps with Hibernates](https://www.baeldung.com/hibernate-persisting-map)
* [The best way to map a One to Many relationship with JPA and Hibernate](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/)
* [Spring Initializr](https://start.spring.io/)
* [Markdown-Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)