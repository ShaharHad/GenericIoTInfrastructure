package Factory;

import java.util.function.Function;

public class Tests {
    public static void main(String[] args) {
        Function<String, Dog> dogConstructor = ((s) -> new Dog(s));
        Function<String, Cat> catConstructor = ((s) -> new Cat(s));
        Function<String, Lion> lionConstructor = ((s) -> new Lion(s));

        CommandFactory<String, Animals, String> factoryAnimal =  new CommandFactory<>();

        factoryAnimal.add("Dog", dogConstructor);
        factoryAnimal.add("Cat", catConstructor);
        factoryAnimal.add("Lion", lionConstructor);

        Animals dog = factoryAnimal.create("Dog", "black");
        Animals cat = factoryAnimal.create("Cat", "white");
        Animals lion = factoryAnimal.create("Lion", "gold");

        System.out.println(dog);
        System.out.println(cat);
        System.out.println(lion);

        factoryAnimal.add("DogAge", dogConstructor);
        factoryAnimal.add("CatAge", catConstructor);
        factoryAnimal.add("LionAge", lionConstructor);

        CommandFactory<String, Integer, Integer> factoryAnimalAge =  new CommandFactory<>();

        Function<Integer, Integer> dogAge = (Dog::convertAgeToDogAge);
        Function<Integer, Integer> catAge = (Cat::convertAgeToCatAge);
        Function<Integer, Integer> lionAge = (Lion::convertAgeToLionAge);

        factoryAnimalAge.add("DogAge", dogAge);
        factoryAnimalAge.add("CatAge", catAge);
        factoryAnimalAge.add("LionAge", lionAge);

        System.out.println(factoryAnimalAge.create("DogAge", 5));
        System.out.println(factoryAnimalAge.create("LionAge", 5));
        System.out.println(factoryAnimalAge.create("CatAge", 5));

        CommandFactory<Object, Object, String> factoryAnimalGetColor =  new CommandFactory<>();

        Function<String, Object> dogGetColor = (dog::getColor);
        Function<String, Object> catGetColor = (cat::getColor);
        Function<String, Object> lionGetColor = (lion::getColor);

        factoryAnimalGetColor.add("DogGetColor", dogGetColor);
        factoryAnimalGetColor.add("CatGetColor", catGetColor);
        factoryAnimalGetColor.add("LionGetColor", lionGetColor);

        System.out.println(factoryAnimalGetColor.create("DogGetColor", "5"));
        System.out.println(factoryAnimalGetColor.create("CatGetColor", "5"));
        System.out.println(factoryAnimalGetColor.create("LionGetColor", "5"));
    }
}

abstract class Animals{
    @Override
    public String toString() {
        return "Animals";
    }
    abstract String getColor(Object o);
}

class Dog extends Animals{
  private String color;

  public Dog(String str){
      color = str;
  }

    public String getColor(Object o) {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Dog have color " + color;
    }

    static int convertAgeToDogAge(Integer age){
      return age * 3;
    }
}

class Lion extends Animals{
    private String color;

    public Lion(String str){
        color = str;
    }

    public String getColor(Object o) {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Lion have color " + color;
    }

    static int convertAgeToLionAge(Integer age){
        return age * 4;
    }
}

class Cat extends Animals{
    private String color;

    public Cat(String str){
        color = str;
    }

    public String getColor(Object o) {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Cat have color " + color;
    }

    static int convertAgeToCatAge(Integer age){
        return age * 5;
    }
}

