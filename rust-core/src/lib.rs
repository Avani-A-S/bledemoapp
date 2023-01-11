pub mod device;
use device::{scan, connect};

pub fn core_test(input: String) -> String {
    let owned_string: String = "from Rust!\n".to_owned();
    return format!("{}{}", owned_string, input);
}

pub fn core_scan() -> String {
    let devices: String = scan().unwrap();
    return format!("{}", devices);
}

pub fn core_connect(device: String) -> String {
    let result: String = connect(device).unwrap();
    return format!("{}", result);
}