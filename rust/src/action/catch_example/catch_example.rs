use futures::FutureExt;

pub async fn catch_example() {
    let test_call = async {
        let _ = 100 / 0;
    };

    let _ = test_call.catch_unwind().await.is_err_and(|e| {
        println!("task has exception = {}", e.downcast_ref::<&str>().unwrap());
        true
    });
    println!("continue!!!")
}
