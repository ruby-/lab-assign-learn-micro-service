FROM python:3-alpine
MAINTAINER r5by dllizhongwei@gmail.com

EXPOSE 9090
ENV PYTHONPATH /app
WORKDIR /app/msg

COPY __init__.py ../
COPY requirements.txt ../
RUN pip install --no-cache-dir -r ../requirements.txt

COPY msg ../msg

ENTRYPOINT ["python", "./message_service.py"]
