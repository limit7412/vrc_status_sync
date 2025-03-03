FROM virtuslab/scala-cli:latest as build-image

WORKDIR /work
COPY ./ ./

RUN scala-cli clean .
RUN scala-cli config power true
RUN scala-cli --power package --native-image -o bootstrap . --main-class main
RUN chmod +x bootstrap

FROM public.ecr.aws/lambda/provided:latest

COPY --from=build-image /work/bootstrap /var/runtime/

CMD ["dummyHandler"]
