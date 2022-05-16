# Codewisdom walmart-6G system

Repo=meixie
Tag=1.0.0

# build image
.PHONY: build
build: clean-image package

.PHONY: package
package:
	@mvn clean package -DskipTests

.PHONY: build-image
build-image:
	@hack/build-image.sh $(Repo) $(Tag)

.PHONY: build-image-arm64
build-image-arm64:
	@hack/build-image-arm64.sh $(Repo) $(Tag)-arm64

# push image
.PHONY: push-image
push-image:
	@hack/push-image.sh $(Repo) $(Tag)

.PHONY: publish-image
publish-image:
	@hack/publish-docker-images.sh $(Repo) $(Tag)

.PHONY: clean
clean:
	@mvn clean
	@hack/clean-image.sh $(Repo)

# clean image
.PHONY: clean-image
clean-image:
	@hack/clean-image.sh $(Repo)
