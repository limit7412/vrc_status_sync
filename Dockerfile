FROM virtuslab/scala-cli:latest as build-image

WORKDIR /work
COPY ./ ./

RUN scala-cli clean .
RUN scala-cli config power true
# 一時的にjvmを使用
RUN scala-cli --power package --assembly --main-class main -o lambda-handler.jar .

FROM public.ecr.aws/lambda/provided:latest

RUN dnf update -y && \
  dnf install -y java-11-amazon-corretto && \
  dnf clean all

COPY --from=build-image /work/google_credential.json /var/runtime/
COPY --from=build-image /work/lambda-handler.jar /var/runtime/

RUN printf '#!/bin/bash\njava -jar /var/runtime/lambda-handler.jar\n' > /var/runtime/bootstrap
RUN chmod +x /var/runtime/bootstrap

CMD ["dummyHandler"]


# RUN scala-cli --power package --native-image -o bootstrap .
# RUN chmod +x bootstrap

# FROM public.ecr.aws/lambda/provided:latest

# COPY --from=build-image /work/google_credential.json /var/runtime/
# COPY --from=build-image /work/bootstrap /var/runtime/

# CMD ["dummyHandler"]
