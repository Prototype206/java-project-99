.PHONY: build

setup:
	./gradlew shadowJar

clean:
	./gradlew clean

test:
	./gradlew test

check:
	./gradlew checkstyleMain checkstyleTest

build:
	./gradlew clean build

start:
	./gradlew bootRun
