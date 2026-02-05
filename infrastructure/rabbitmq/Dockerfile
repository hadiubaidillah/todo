FROM rabbitmq:4.0-management

ADD --chmod=644 https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v4.0.7/rabbitmq_delayed_message_exchange-v4.0.7.ez /opt/rabbitmq/plugins/
RUN rabbitmq-plugins enable rabbitmq_delayed_message_exchange
