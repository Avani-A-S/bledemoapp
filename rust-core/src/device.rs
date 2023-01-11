
#![allow(unused_imports)]
use std::error::Error;
use std::time::Duration;
use tokio::time;

use btleplug::api::{bleuuid::uuid_from_u16, Central, Manager as _, Peripheral as _, ScanFilter, WriteType};
use btleplug::platform::{Adapter, Manager, Peripheral};

use android_logger::Config;
use log::Level;
use futures::stream::StreamExt;

#[tokio::main]
pub async fn scan() -> Result<String, Box<dyn Error>> {

    #[cfg(target_os = "android")]
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Info)
            .with_tag("Rust"),
    );
    log_panics::init();

    log::info!("device.rs -> scan(): Initialize Manager");
    let manager = Manager::new().await?;

    log::info!("device.rs -> scan(): Get central");
    let adapter_list = manager.adapters().await.expect("Can't find adapter.");

    if adapter_list.is_empty() {
        log::info!("device.rs -> scan(): No Bluetooth adapters found");
    }

    log::info!("device.rs -> scan(): Iterate through adapter");
    let mut devices: String = "".to_owned();
    for adapter in adapter_list.iter() {
        log::info!("Starting scan on {}...", adapter.adapter_info().await?);
        adapter
            .start_scan(ScanFilter::default())
            .await
            .expect("Can't scan BLE adapter for connected devices...");
        time::sleep(Duration::from_secs(30)).await;
        let peripherals = adapter.peripherals().await?;

        if peripherals.is_empty() {
            log::info!("->>> BLE peripheral devices were not found, sorry. Exiting...");
        } else {
            // All peripheral devices in range
            for peripheral in peripherals.iter() {
                let properties = peripheral.properties().await?;
                //let is_connected = peripheral.is_connected().await?;
                let address = properties.as_ref().unwrap().address;
                let local_name = properties
                    .unwrap()
                    .local_name
                    .unwrap_or(address.to_string());

                    log::info!("Device: {:?}", local_name);

                    if devices != "" {
                        devices.push_str("|");
                    }
                    devices.push_str(&local_name);
            }
        }
        adapter.stop_scan().await.expect("BLE adapter stop scanning error...");
    }
    Ok(devices)
}

#[tokio::main]
pub async fn connect(name: String) -> Result<String, Box<dyn Error>> {
    
    let mut result: String = "".to_owned();
    let manager = Manager::new().await?;
    let adapter_list = manager.adapters().await?;
    if adapter_list.is_empty() {
        log::info!("No Bluetooth adapters found");
    }

    let central = adapter_list
        .into_iter()
        .nth(0)
        .unwrap();
    central.start_scan(ScanFilter::default()).await.unwrap();
    time::sleep(Duration::from_secs(30)).await;
    for device in central.peripherals().await.unwrap() {
        
        if let Some(devicename) = device.properties().await?.unwrap().local_name {
            if devicename.starts_with(&name) {
                log::info!("Connecting to peripheral {:?}.", &name);
                
                if let Err(err) = device.connect().await {
                    time::sleep(Duration::from_secs(30)).await;
                    log::info!("Error");
                    log::info!("Error connecting to peripheral, skipping: {}", err);
                    continue;
                }
                result.push_str("Connected Successfully.");
                log::info!("Connected Successfully.");
            }
        }
    }
    central.stop_scan().await.expect("BLE adapter stop scanning error...");

    Ok(result)
}

#[tokio::main]
pub async fn disconnect(name: String) -> Result<(), Box<dyn Error>> {
    let manager = Manager::new().await?;
    let adapter_list = manager.adapters().await?;
    if adapter_list.is_empty() {
        log::info!("No Bluetooth adapters found");
    }

    for adapter in adapter_list.iter() {
        log::info!("Starting scan on {}...", adapter.adapter_info().await?);
        adapter
            .start_scan(ScanFilter::default())
            .await
            .expect("Can't scan BLE adapter for connected devices...");
        time::sleep(Duration::from_secs(10)).await;
        let peripherals = adapter.peripherals().await?;

        if peripherals.is_empty() {
            log::info!("->>> BLE peripheral devices were not found, sorry. Exiting connect()...");
        } else {
            // All peripheral devices in range
            for peripheral in peripherals.iter() {
                let properties = peripheral.properties().await?;
                let is_connected = peripheral.is_connected().await?;
                let local_name = properties
                    .unwrap()
                    .local_name
                    .unwrap_or(String::from("(peripheral name unknown)"));

                if local_name == name {

                    if is_connected {

                        log::info!("Disconnecting from peripheral {:?}.", &local_name);
                        peripheral
                            .disconnect()
                            .await
                            .expect("Error disconnecting from BLE peripheral");
                    }
                }
            }
        }
    }
    Ok(())
}