package me.akrivos

import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, OffsetResetStrategy}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.requests.IsolationLevel

import scala.collection.JavaConverters._
import scala.util.Random

object KafkaZookeeper extends App {

  def createProducer(bootstrapServers: String = "localhost:9092"): KafkaProducer[String, String] = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384")
    props.put(ProducerConfig.LINGER_MS_CONFIG, "1")
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432")
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "false")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    new KafkaProducer(props)
  }

  def createConsumer(bootstrapServers: String = "localhost:9092", groupId: String = "test"): KafkaConsumer[String, String] = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000")
    props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, ConsumerConfig.DEFAULT_MAX_PARTITION_FETCH_BYTES.toString)
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500")
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000")
    props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, "300000")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.toString.toLowerCase)
    props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_UNCOMMITTED.toString.toLowerCase)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    new KafkaConsumer(props)
  }

  val producer = createProducer()
  val consumer = createConsumer()

  val topic = "test"
  val n = 10

  println(s"Producing to topic $topic: numbers 1 to $n")
  (1 to n).foreach { x =>
    producer.send(new ProducerRecord(topic, Random.alphanumeric.take(12).mkString, s"MSG-$x"))
  }
  producer.flush()

  println(s"Consuming from topic $topic:")
  consumer.subscribe(List(topic).asJava)
  var consumed = 0
  while (consumed < n) {
    val records = consumer.poll(1000).records(topic).asScala.toList
    consumed = consumed + records.size
    records.foreach { record =>
      println(s"-> ${record.key()} :: ${record.value()}")
    }
  }
  System.exit(0)
}
