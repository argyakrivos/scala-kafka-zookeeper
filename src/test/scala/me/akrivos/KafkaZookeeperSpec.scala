package me.akrivos

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.scalatest._

import scala.collection.JavaConverters._
import scala.util.Random

class KafkaZookeeperSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  val kafka = new KafkaServer()
  val bootstrapServers = s"localhost:${kafka.kafkaPort}"

  override protected def beforeAll(): Unit = {
    kafka.startup()
  }

  override protected def afterAll(): Unit = {
    kafka.close()
  }

  "Kafka producer" should "produce 10 messages in a topic" in {
    val topic = "xxx"
    // create producer
    val producer = KafkaZookeeper.createProducer(bootstrapServers)
    (1 to 10).foreach { x =>
      producer.send(new ProducerRecord(topic, Random.alphanumeric.take(12).mkString, s"MSG-$x"))
    }
    producer.flush()

    // verify results
    val records = kafka.consume(topic, 10, 5000, new StringDeserializer, new StringDeserializer)
    records.length shouldBe 10

    // cleanup
    producer.close()
  }

  "Kafka consumer" should "consumer 10 messages from a topic" in {
    val topic = "yyy"
    // create consumer
    val consumer = KafkaZookeeper.createConsumer(bootstrapServers)
    consumer.subscribe(List(topic).asJava)

    val records = (1 to 10).map { x =>
      new ProducerRecord(topic, Random.alphanumeric.take(12).mkString, s"MSG-$x")
    }
    kafka.produce(topic, records, new StringSerializer, new StringSerializer)

    // verify results
    val received = consumer.poll(5000).records(topic).asScala.toList
    received.length shouldBe 10

    // cleanup
    consumer.close()
  }
}
