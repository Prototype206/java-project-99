.PHONY: build

setup:
	./gradlew clean build -x test

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
