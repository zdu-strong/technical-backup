use crate::enums::animal_enum::AnimalEnum;
use serde_json::to_string_pretty;
use std::str::FromStr;
use strum::IntoEnumIterator;

pub async fn print_all_animal() {
    let ref mut animal_list = AnimalEnum::iter().collect::<Vec<_>>();
    println!("All animal is {}", to_string_pretty(animal_list).unwrap());
    println!(
        "Parse string to animal: {}",
        AnimalEnum::from_str("DOG").unwrap()
    );
    println!("Print animal value: {}", AnimalEnum::TIGER.as_ref());
}
