---
sidebar_position: 2
title: Validation
---

ZIO Prelude provides a Validation monad that is based upon ZPure. It is used to help validate data for your application.
A Validation can be composed with other validations. Doing so will result in all validations being run without a short
circuit. So the results you get are a complete list of validation errors or successful values.

You create a Validation result by invoking `succeed` or `fail` on `Validation`

```scala mdoc
import zio.prelude._

case class Student(age: Int, name: String)

object MyValidations {

    def validateDrinkingAge(age: Int) = {
      if (age > 21) {
        Validation.succeed(age)
      } else {
        Validation.fail("Too young to drink alcohol!")
      }
    }
  
  def validateName(name: String) = {
    if (name.nonEmpty) {
      Validation.succeed(name)
    } else {
      Validation.fail("Name must be provided!")
    }
  }
  
  def validateStudent(student: Student) = 
    Validation.validate(validateDrinkingAge(student.age), validateName(student.name))
  
}

val ash = Student(23, "Ash")
val Sheila = Student(19, "Sheila")
val evil = Student(16, "")
// Fail!
MyValidations.validateDrinkingAge(19)
// Success!
MyValidations.validateDrinkingAge(23)

// composed - Success
MyValidations.validateStudent(ash)
// composed - failures
MyValidations.validateStudent(Sheila)
MyValidations.validateStudent(evil)
```

You can take advantage of ZPure's built in logging (think: Writer monad) if you'd like a full trace of what happened
during validation:

```scala mdoc

object LoggedValidations {
  def validateDrinkingAge(age: Int) = {
    if (age > 21) {
      Validation.succeed(age).log("Validated age")
    } else {
      Validation.fail("Too young to drink alcohol!").log("Age failed validation!")
    }
  }

  def validateName(name: String) = {
    if (name.nonEmpty) {
      Validation.succeed(name).log("Validated name")
    } else {
      Validation.fail("Name must be provided!").log("Name failed validation!")
    }
  }

  def validateStudent(student: Student) =
    Validation.validate(validateDrinkingAge(student.age), validateName(student.name))

}

// composed - Success
LoggedValidations.validateStudent(ash)
// composed - failures
LoggedValidations.validateStudent(Sheila)
LoggedValidations.validateStudent(evil)

```
