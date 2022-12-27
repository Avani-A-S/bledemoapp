pub mod device;

use device::scan;

pub fn core_test(input: String) -> String {
    let owned_string: String = "from Rust Library!\n".to_owned();
    return format!("{}{}", owned_string, input);
}

pub fn core_scan() -> String {

    scan().unwrap();

    let owned_string: String = "Scan Complete!\n".to_owned();
    return format!("{}", owned_string);
}