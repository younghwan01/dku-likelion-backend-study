# 🦁 Week05 Java Study

이번 주차에는 Java의 **객체지향 프로그래밍**과 **예외 처리**를 학습했습니다.

---

## 📌 학습 내용

| 구분 | 내용 |
|---|---|
| 객체지향 | 클래스, 인스턴스, 멤버 변수, 메소드 |
| 생성자 | 기본 생성자, 생성자 오버로딩, `this` |
| 접근 제어자 | `private`, getter/setter |
| 상속 | `extends`, 오버로딩, 오버라이딩 |
| 인터페이스 | `implements`, 인터페이스 구현 |
| 예외 처리 | `try-catch-finally`, 여러 예외 처리 |

---

# Part 1. 객체지향

## 📚 객체지향이란?

객체지향은 현실 세계의 사물을 **속성**과 **기능**으로 나누어 코드로 표현하는 방식입니다.

| 현실 세계 | Java에서의 표현 |
|---|---|
| 속성 | 멤버 변수 |
| 기능 | 메소드 |

---

## 🏦 클래스와 멤버 변수

`BankAccount.java`에서는 은행 계좌를 클래스로 만들고, 계좌 정보를 멤버 변수로 작성했습니다.

```java
public class BankAccount {

    //멤버변수
    // private
    private int bankCode;
    private int accountNo;
    private String owner;
    private int balance;
    private boolean isDormant;
    private int password;
}
```

| 멤버 변수 | 의미 |
|---|---|
| `bankCode` | 은행 코드 |
| `accountNo` | 계좌 번호 |
| `owner` | 소유자 |
| `balance` | 잔액 |
| `isDormant` | 휴면 계좌 여부 |
| `password` | 계좌 비밀번호 |

---

## 🧱 메소드

은행 계좌가 할 수 있는 동작은 메소드로 작성했습니다.

```java
//메소드
public void inquiry(){}
public void deposit(){}
public void heldInDormant(){}
public void changePassword(int password){
    this.password = password;
}
```

---

## 🛠 생성자

생성자는 인스턴스를 만들 때 실행됩니다.
생성자의 이름은 클래스 이름과 같아야 합니다.

```java
BankAccount(){

}
BankAccount(int bankCode,
            int accountNo,
            String owner,
            int balance,
            int password,
            boolean isDormant
){
    this.bankCode = bankCode;
    this.accountNo = accountNo;
    this.owner = owner;
    this.balance = balance;
    this.password = password;
    this.isDormant = isDormant;
}
```

| 키워드 | 의미 |
|---|---|
| `new` | 인스턴스 생성 |
| `this` | 현재 인스턴스 자기 자신 |
| 생성자 오버로딩 | 매개변수가 다른 생성자를 여러 개 작성 |

---

## 🔒 접근 제어자

`private`으로 선언된 변수는 클래스 밖에서 직접 접근할 수 없습니다.
따라서 메소드를 통해 값을 변경하고 조회해야 합니다.

```java
public class ClassExample {
    public static void main(String[] args) {
        BankAccount bankAccount = new BankAccount();
//        bankAccount.password = 123456;
        bankAccount.changePassword(123456);
        System.out.println(bankAccount.getPassword());
    }
}
```

```java
public int getPassword() {
    return password;
}

public void setPassword(int password) {
    this.password = password;
}
```

---

## 🧬 상속 extends

`extends`는 부모 클래스의 속성과 기능을 자식 클래스가 물려받을 때 사용합니다.

```java
public class SubscriptionAccount extends BankAccount{
    int numOfSubscription;
}
```

---

## 🔁 오버로딩과 오버라이딩

`DollarAccount.java`에서는 `BankAccount`를 상속받고, 오버로딩과 오버라이딩을 학습했습니다.

```java
public class DollarAccount extends BankAccount{

    //오버로딩 => 부모 클래스에서 상속받은 메서드에서 파라미터를 변경
    // 새로운 메서드 정의!
    void transfer(double currencyRate){}

    //오버라이딩 => 부모 클래스에서 상속받은 메서드의 내용 변경
    //자식 클래스의 상황에 맞게
    public void deposit(){

    }
}
```

| 구분 | 의미 |
|---|---|
| 오버로딩 | 메소드 이름은 같고 파라미터를 다르게 작성 |
| 오버라이딩 | 부모 메소드의 내용을 자식 클래스에 맞게 변경 |

---

## 🧩 인터페이스 implements

인터페이스는 클래스가 반드시 구현해야 하는 기능을 정해놓은 규칙입니다.

```java
public interface Withdrawable {

    public void withdraw();
}
```

`SavingsAccount.java`에서는 `Withdrawable` 인터페이스를 구현했습니다.

```java
public class SavingsAccount extends BankAccount implements Withdrawable{

    boolean isOverdraft;
    void transfer(){};
    public void withdraw(){
        System.out.println("Withdraw");
    };
}
```

| 키워드 | 의미 |
|---|---|
| `extends` | 부모 클래스 상속 |
| `implements` | 인터페이스 구현 |

---

## 💡 내가 헷갈렸다가 이해한 부분

| 헷갈린 부분 | 이해한 내용 |
|---|---|
| `extends` | 생성자를 가져오는 것이 아니라 부모 클래스의 속성과 기능을 상속받는 것 |
| 생성자 | 상속되는 것이 아니라 자식 객체 생성 시 부모 생성자가 먼저 실행됨 |
| `private` 필드 | 상속받아도 직접 접근할 수 없음 |
| `implements` | 인터페이스의 규칙을 지키겠다고 선언하는 것 |

---

# Part 2. 예외 처리

## ⚠️ 예외란?

예외는 프로그램 실행 중 발생할 수 있는 오류입니다.

| 예외 상황 | 예외 종류 |
|---|---|
| 0으로 나누기 | `ArithmeticException` |
| 인덱스 범위 초과 | `IndexOutOfBoundsException` |
| 잘못된 인자 전달 | `IllegalArgumentException` |

---

## 🧪 try-catch-finally

`ExceptionExample.java`에서 예외 처리를 실습했습니다.

```java
try {
//          arrayList.get(10);
    int a = 10;
    int b = 0;
    int c = a / b;
} catch (IndexOutOfBoundsException ioe){
    System.out.println("IndexOutOfBoundsException 발생");
} catch (IllegalArgumentException iae) {
    System.out.println("IllegalArgumentException 발생");
} catch (Exception e){
    System.out.println("Exception 발생");
} finally {
    System.out.println("finally");
}
```

| 구문 | 역할 |
|---|---|
| `try` | 예외가 발생할 수 있는 코드 작성 |
| `catch` | 발생한 예외를 잡아서 처리 |
| `finally` | 예외 발생 여부와 관계없이 항상 실행 |

---

## 📌 여러 예외 처리하기

예외는 종류별로 다르게 처리할 수 있습니다.
여러 예외를 처리할 때는 구체적인 예외를 먼저 작성하고, 마지막에 가장 상위 예외인 `Exception`을 작성합니다.

```java
catch (IndexOutOfBoundsException ioe){
    System.out.println("IndexOutOfBoundsException 발생");
} catch (IllegalArgumentException iae) {
    System.out.println("IllegalArgumentException 발생");
} catch (Exception e){
    System.out.println("Exception 발생");
}
```

---

## 📁 실습 파일

| 파일명 | 내용 |
|---|---|
| `BankAccount.java` | 은행 계좌 클래스 |
| `ClassExample.java` | 객체 생성, private 접근 실습 |
| `DollarAccount.java` | 상속, 오버로딩, 오버라이딩 |
| `SavingsAccount.java` | 상속, 인터페이스 구현 |
| `SubscriptionAccount.java` | 청약 계좌 클래스 |
| `Withdrawable.java` | 출금 인터페이스 |
| `ExceptionExample.java` | 예외 처리 |
| `Main.java` | 기본 실행 예제 |

---

## ✅ 정리

이번 주차에는 객체지향과 예외 처리를 학습했습니다.

객체지향에서는 현실의 사물을 속성과 기능으로 나누어 클래스로 표현했고,
상속과 인터페이스를 통해 클래스 간 관계를 이해했습니다.

예외 처리에서는 `try-catch-finally`를 사용해
프로그램 실행 중 발생할 수 있는 오류를 처리하는 방법을 배웠습니다.
