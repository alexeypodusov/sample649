package ru.alexeypodusov.sample649

import android.app.Application
import ru.alexeypodusov.sample649.charactersfeature.di.CharactersComponent
import ru.alexeypodusov.sample649.charactersfeature.di.DaggerCharactersComponent
import ru.alexeypodusov.sample649.charactersfeature.di.CharactersComponentProvider
import ru.alexeypodusov.sample649.di.AppComponent
import ru.alexeypodusov.sample649.di.DaggerAppComponent

class App: Application(), CharactersComponentProvider {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
    }

    override fun provideCharactersComponent(): CharactersComponent {
        return DaggerCharactersComponent.builder()
            .characterDependencies(appComponent)
            .build()
    }
}