FROM gradle:5.0-jdk8-alpine as builder

ARG PROXY=""

ENV GRADLE_OPTS="-Xmx128m -Dorg.gradle.jvmargs='-Xmx256m -XX:MaxPermSize=64m'"

ADD . /home/project/

USER root

WORKDIR /home/project/

RUN \
	rm -rf .git .gradle && \
	if [ -n "$PROXY" ]; then \
		echo -e "\e[33mUsing proxy $PROXY\e[0m"; \
		export proxy=$PROXY; \
		export http_proxy=$PROXY; \
		export https_proxy=$PROXY; \
		echo "systemProp.http.proxyHost=$(echo $PROXY | grep -Eo '([0-9\.]+)' | head -1)" >> ~/.gradle/gradle.properties; \
		echo "systemProp.https.proxyHost=$(echo $PROXY | grep -Eo '([0-9\.]+)' | head -1)" >> ~/.gradle/gradle.properties; \
		echo "systemProp.http.proxyPort=$(echo $PROXY | grep -Eo '([0-9\.]+)' | tail -1)" >> ~/.gradle/gradle.properties; \
		echo "systemProp.https.proxyPort=$(echo $PROXY | grep -Eo '([0-9\.]+)' | tail -1)" >> ~/.gradle/gradle.properties; \
		cat ~/.gradle/gradle.properties; \
	else \
		echo -e "\e[33mNot using proxy. If needed use \e[92m'--build-arg PROXY=http://172.17.0.1:3128'\e[33m with 'docker build'\e[0m"; \
	fi && \
	\
    echo -e "\e[32mBuilding application...\e[0m" && gradle build; \
	echo -e "\e[32mCopying distribution...\e[0m" && mkdir /home/build && cp ./build/distributions/*.zip /home/build/ && \
	echo -e "\e[32mEnding build phase.\e[0m";


FROM openjdk:8-jre-alpine

WORKDIR /home/project/

COPY --from=builder /home/build/* ./

RUN unzip -q *.zip && rm -rf *.zip

VOLUME /tmp/ignite-test

CMD PROJECT_NAME=$(find ./ -mindepth 1 -maxdepth 1 -type d -exec basename '{}' \;) && \
    $PROJECT_NAME/bin/$(echo $PROJECT_NAME | grep -oE "^(-?[a-zA-Z])+")