<template>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col col-md-6 col-lg-4 col-xl-3 py-4">
                <main class="form-signin w-100 m-auto">
                    <form>
                        <h1 class="h3 fw-normal">Sign in</h1>
                        <p class="text-body-secondary">Please sign in with your credentials.</p>

                        <div class="form-floating my-1">
                            <input type="text" class="form-control" name="email" placeholder="Email address">
                            <label for="email">Email</label>
                        </div>

                        <div class="form-floating my-1">
                            <input type="password" class="form-control" name="password" placeholder="Password">
                            <label for="password">Password</label>
                        </div>

                        <button class="btn btn-primary w-100 py-2 my-3" type="submit">Sign in</button>
                    </form>
                </main>
            </div>
        </div>
    </div>
</template>
