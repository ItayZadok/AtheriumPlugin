# AtheriumPlugin - Refactored Minecraft Plugin

A comprehensive Minecraft plugin featuring a GUI system, stat system, and class abilities. This project has been completely refactored to follow SOLID principles and modern design patterns.

## 🏗️ Architecture Overview

The plugin now follows a clean, modular architecture with clear separation of concerns:

### Core Systems

- **GameEngine**: Central coordinator implementing the Facade pattern
- **GameLoop**: Efficient game loop management replacing the old PlayerScheduler
- **StatSystem**: High-performance stat management with caching
- **CombatSystem**: Organized combat handling with separate concerns
- **AbilitySystem**: Centralized ability management
- **PlayerSystem**: Player data and profile management
- **ConfigurationManager**: Robust configuration handling
- **ItemSystem**: Item management and updates
- **GuiSystem**: GUI management and menu handling

### Key Improvements

#### 1. **Performance Optimizations**
- **Stat Caching**: Implemented intelligent caching for stat calculations
- **Efficient Collections**: Replaced inefficient data structures with optimized alternatives
- **Reduced Memory Allocations**: Minimized object creation in hot paths
- **Batch Processing**: Grouped operations for better performance

#### 2. **SOLID Principles**
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Easy to extend without modifying existing code
- **Liskov Substitution**: Proper inheritance hierarchies
- **Interface Segregation**: Clean, focused interfaces
- **Dependency Inversion**: Dependencies flow toward abstractions

#### 3. **Design Patterns**
- **Facade Pattern**: GameEngine provides a clean interface
- **Strategy Pattern**: Different combat and ability strategies
- **Observer Pattern**: Event-driven architecture
- **Factory Pattern**: Object creation management
- **Builder Pattern**: Complex object construction

## 📁 Project Structure

```
src/main/java/org/atheriumPlugin/
├── core/                          # Core engine and game loop
│   ├── GameEngine.java           # Main coordinator (Facade)
│   └── GameLoop.java             # Centralized game loop
├── stats/                         # Stat management system
│   ├── StatSystem.java           # Central stat coordinator
│   ├── EntityStats.java          # Individual entity stats
│   ├── StatModifier.java         # Stat modification tracking
│   ├── DamageIndicatorManager.java # Damage display management
│   └── CustomStat.java           # Stat definitions
├── combat/                        # Combat system
│   ├── CombatSystem.java         # Combat coordination
│   ├── CombatListener.java       # Event handling
│   ├── CombatState.java          # Combat state tracking
│   ├── DamageCalculator.java     # Damage calculations
│   └── ParticleEffectManager.java # Visual effects
├── abilities/                     # Ability system
│   ├── AbilitySystem.java        # Ability management
│   ├── AbilityBase.java          # Base ability class
│   ├── PlayerAbility.java        # Player ability interface
│   └── [ability implementations]/
├── player/                        # Player management
│   ├── PlayerSystem.java         # Player coordination
│   ├── PlayerProfile.java        # Player data container
│   └── [existing player classes]/
├── config/                        # Configuration management
│   ├── ConfigurationManager.java # Config handling
│   └── ConfigFile.java           # Config file definitions
├── items/                         # Item system
│   ├── ItemSystem.java           # Item management
│   └── [existing item classes]/
├── gui/                          # GUI system
│   ├── GuiSystem.java            # GUI coordination
│   └── [existing GUI classes]/
└── AtheriumPlugin.java           # Main plugin class
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Spigot/Paper 1.20+ server
- Maven for building

### Building
```bash
mvn clean package
```

### Installation
1. Build the project
2. Copy `target/atheriumplugin-1.0-SNAPSHOT.jar` to your server's `plugins/` folder
3. Start/restart your server
4. Configure the plugin as needed

## 🔧 Configuration

The plugin uses a modular configuration system:

- **Player Ability Config**: Player ability data and progression
- **Item Configs**: Custom item definitions and properties
- **Stat Configs**: Stat balancing and modifiers

## 🎮 Features

### Stat System
- **Efficient Calculation**: Cached stat calculations for performance
- **Modifier System**: Flexible flat and percentage modifiers
- **Duration Tracking**: Time-based stat modifications
- **Damage Indicators**: Visual damage feedback

### Combat System
- **Organized Combat**: Clean separation of combat concerns
- **Particle Effects**: Visual feedback for different attack types
- **Combat State**: Tracking of combat interactions
- **Damage Calculation**: Sophisticated damage formulas

### Ability System
- **Modular Abilities**: Easy to add new abilities
- **Class-Based**: Different ability sets per class
- **Cooldown Management**: Proper ability timing
- **Event-Driven**: Clean ability activation

### GUI System
- **Menu Management**: Organized menu system
- **Class Selection**: Visual class choosing interface
- **Ability Selection**: Ability management interface
- **Stats Display**: Player stat visualization

## 🛠️ Development Guide

### Adding New Abilities

1. Create a new class extending `AbilityBase`:
```java
public class MyNewAbility extends AbilityBase {
    @Override
    public void execute(Player player, Event event) {
        // Ability logic here
    }
    
    @Override
    public boolean canActivate(Player player, Event event) {
        // Activation conditions
        return true;
    }
}
```

2. Register in `AbilityData` enum:
```java
MY_NEW_ABILITY(MyNewAbility.class, "My New Ability", "Description")
```

### Adding New Stats

1. Add to `CustomStat` enum:
```java
NEW_STAT {
    @Override
    public void set(LivingEntity entity, double value) {
        // Apply stat to entity
    }
    
    @Override
    public String getDisplayName() {
        return "New Stat";
    }
}
```

### Adding New Combat Types

1. Extend `CombatSystem` with new methods
2. Add corresponding event handling in `CombatListener`
3. Implement visual effects in `ParticleEffectManager`

## 📊 Performance Considerations

### Stat System Performance
- **Caching**: Stats are cached for 50ms to reduce calculations
- **Batch Updates**: Multiple stat changes are batched
- **Efficient Collections**: Uses `ConcurrentHashMap` for thread safety
- **Memory Management**: Automatic cleanup of expired modifiers

### Combat Performance
- **Event Filtering**: Only processes relevant combat events
- **Particle Optimization**: Efficient particle spawning
- **State Management**: Minimal combat state tracking

### Memory Management
- **Automatic Cleanup**: Dead entities and expired data are cleaned up
- **Profile Management**: Player profiles are removed on quit
- **Resource Pooling**: Reuses objects where possible

## 🔄 Migration from Old System

The refactored system maintains compatibility with existing data while providing:

1. **Backward Compatibility**: Existing configs and data work unchanged
2. **Gradual Migration**: Can be deployed alongside old system
3. **Data Preservation**: All player progress is maintained
4. **Feature Parity**: All existing features are preserved

## 🤝 Contributing

When contributing to this project:

1. Follow SOLID principles
2. Add comprehensive documentation
3. Include unit tests for new features
4. Maintain performance standards
5. Use the established patterns

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Check the documentation
- Review the code examples
- Open an issue for bugs
- Submit feature requests

---

**Note**: This refactored version provides a solid foundation for future expansion while maintaining all existing functionality. The new architecture makes it easy to add new features, optimize performance, and maintain code quality. 