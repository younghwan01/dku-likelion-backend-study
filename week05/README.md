# Week04 - Java 객체지향 / 예외 처리

## 학습 목표

이번 주차에는 Java의 객체지향 프로그래밍과 예외 처리 방법을 학습하였다.

은행 계좌 예제를 중심으로 클래스, 객체, 캡슐화, 생성자, 상속, 오버로딩/오버라이딩, 인터페이스를 실습하였고, `try-catch-finally`를 사용해 예외를 처리하는 방법을 확인하였다.

## 학습 내용

### 1. 객체지향 프로그래밍

객체지향 프로그래밍은 프로그램을 여러 객체의 역할과 책임으로 나누어 구성하는 방식이다.

이번 실습에서는 `BankAccount` 클래스를 만들고, 계좌 정보를 멤버 변수로 저장한 뒤 입금, 조회, 비밀번호 변경 등의 기능을 메서드로 분리하였다.

```java
public class BankAccount {
    private int bankCode;
    private int accountNo;
    private String owner;
    private int balance;
    private boolean isDormant;
    private int password;
}
```

클래스는 객체를 만들기 위한 설계도이고, 객체는 클래스를 기반으로 실제 생성된 데이터이다.

```java
BankAccount bankAccount = new BankAccount();
```

### 2. 캡슐화와 생성자

계좌 비밀번호처럼 외부에서 직접 접근하면 안 되는 값은 `private`으로 선언하였다.

외부에서는 필드에 직접 접근하지 않고, 메서드를 통해 값을 변경하거나 조회하도록 구현하였다.

```java
private int password;

public void changePassword(int password) {
    this.password = password;
}
```

생성자는 객체가 생성될 때 실행되며, 객체의 초기값을 설정할 때 사용한다.

```java
BankAccount(int bankCode, int accountNo, String owner, int balance, int password, boolean isDormant) {
    this.bankCode = bankCode;
    this.accountNo = accountNo;
    this.owner = owner;
    this.balance = balance;
    this.password = password;
    this.isDormant = isDormant;
}
```

`this`는 현재 객체 자기 자신을 의미하며, 멤버 변수와 매개변수 이름이 같을 때 구분하기 위해 사용하였다.

### 3. 상속과 인터페이스

상속은 부모 클래스의 속성과 기능을 자식 클래스가 물려받아 사용하는 개념이다.

공통 계좌 기능은 `BankAccount`에 작성하고, 계좌 종류별 특징은 자식 클래스에서 확장하였다.

```java
public class SavingsAccount extends BankAccount implements Withdrawable {
    public void withdraw() {
        System.out.println("Withdraw");
    }
}
```

- `extends`: 부모 클래스를 상속받을 때 사용
- `implements`: 인터페이스를 구현할 때 사용

이번 실습에서는 `SavingsAccount`, `DollarAccount`, `SubscriptionAccount`가 `BankAccount`를 상속받도록 구현하였다.

### 4. 오버라이딩과 오버로딩

오버라이딩은 부모 클래스의 메서드를 자식 클래스에 맞게 다시 구현하는 방식이다.

```java
@Override
public void deposit() {
}
```

오버로딩은 같은 이름의 메서드를 매개변수만 다르게 여러 개 정의하는 방식이다.

```java
void transfer(double currencyRate) {
}
```

두 개념 모두 메서드와 관련되어 있지만, 오버라이딩은 기존 기능을 재정의하는 것이고 오버로딩은 같은 이름으로 다양한 입력을 처리하는 것이다.

### 5. 예외 처리

예외 처리는 프로그램 실행 중 발생할 수 있는 오류 상황을 처리하는 방법이다.

```java
try {
    int a = 10;
    int b = 0;
    int c = a / b;
} catch (ArithmeticException e) {
    System.out.println("ArithmeticException 발생");
} catch (Exception e) {
    System.out.println("Exception 발생");
} finally {
    System.out.println("finally");
}
```

- `try`: 예외가 발생할 수 있는 코드 작성
- `catch`: 발생한 예외 처리
- `finally`: 예외 발생 여부와 관계없이 항상 실행

여러 예외를 처리할 때는 구체적인 예외를 먼저 작성하고, 마지막에 상위 예외인 `Exception`을 작성해야 한다.

## 헷갈렸던 점

| 헷갈린 점 | 이해한 내용 |
| --- | --- |
| `extends` | 생성자를 가져오는 것이 아니라 부모 클래스의 필드와 메서드를 물려받는 것 |
| 생성자 | 상속되는 것이 아니라 자식 객체 생성 시 부모 생성자가 먼저 실행되는 것 |
| `private` 필드 | 상속받아도 직접 접근할 수 없고 메서드를 통해 접근해야 하는 것 |
| 오버라이딩 / 오버로딩 | 오버라이딩은 재정의, 오버로딩은 매개변수를 다르게 한 메서드 추가 |

## 느낀 점

객체지향 프로그래밍은 단순히 코드를 작성하는 것이 아니라, 객체의 역할과 관계를 설계하는 과정이라는 점을 이해할 수 있었다.

또한 예외 처리를 사용하면 프로그램 실행 중 오류가 발생하더라도 흐름을 안정적으로 관리할 수 있다는 것을 알게 되었다.
