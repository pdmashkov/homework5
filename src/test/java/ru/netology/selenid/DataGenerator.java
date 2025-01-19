package ru.netology.selenid;

import com.github.javafaker.Faker;

import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {
    private DataGenerator() {
    }

    public static String generateDate(int shift, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        return LocalDate.now().plusDays(shift).format(formatter);
    }

    public static String generateCity(String locale) {
        Faker faker = new Faker(new Locale(locale));

        return faker.address().city();
    }

    public static String generateCityRegionRU() {
        Random random = new Random();

        List<String> city = new ArrayList<>();

        city.add("Майкоп");
        city.add("Горно-Алтайск");
        city.add("Уфа");
        city.add("Улан-Удэ");
        city.add("Махачкала");

        return city.get(random.nextInt(city.size()));
    }

    public static void main(String[] args) {
        System.out.println(generateCity("ru"));
    }

    public static String generateName(String locale) {
        Faker faker = new Faker(new Locale(locale));

        return faker.name().fullName();
    }

    public static String generatePhone(String locale) {
        Faker faker = new Faker(new Locale(locale));

        return faker.phoneNumber().phoneNumber();
    }

    @Value
    public static class UserInfo {
        String city;
        String name;
        String phone;
    }

    public static class Registration {
        private Registration() {
        }

        public static UserInfo generateUser(String locale) {
            return new UserInfo(generateCityRegionRU(), generateName(locale), generatePhone(locale));
        }
    }
}
