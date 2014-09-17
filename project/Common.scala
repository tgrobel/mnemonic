import sbt._
import Keys._

object Common {
  def version = "master-SNAPSHOT"
  def organization = "com.tomekgrobel.mnemonic"
  def playVersion = System.getProperty("play.version", "2.3.4")
  def scalaVersion = System.getProperty("scala.version", "2.11.2")
  def scoverageVersion = System.getProperty("scoverage.version", "0.99.7.1")
}