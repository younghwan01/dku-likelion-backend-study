public class SavingsAccount extends BankAccount implements Withdrawable{

    boolean isOverdraft;
    void transfer(){};
    public void withdraw(){
        System.out.println("Withdraw");
    };
}
