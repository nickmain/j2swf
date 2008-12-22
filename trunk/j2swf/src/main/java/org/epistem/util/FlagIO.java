package org.epistem.util;

import java.lang.reflect.Field;
import java.util.*;


/**
 * Utility for parsing flags to produce enums. Also regenerates the bits.
 * 
 * @author nickmain
 *
 * @see Flag
 */
public final class FlagIO<E extends Enum<E>> {

    //parser cache
    private static final Map<Class<?>, FlagIO<?>> parsers = new HashMap<Class<?>, FlagIO<?>>();
    
	private final Class<E> enumClass;
	private final Map<Integer, E> bits2enums = new HashMap<Integer, E>();
	private final Map<E, Integer> enums2bits;
	
	/**
	 * Get a parser for the given class
	 */
	private static <T extends Enum<T>> FlagIO<T> forClass( Class<T> clazz ) {
	    @SuppressWarnings("unchecked")
	    FlagIO<T> parser = (FlagIO<T>) parsers.get( clazz );
	    if( parser == null ) {
	        parsers.put( clazz, parser = new FlagIO<T>( clazz ));
	    }
	    return parser;
	}
	
	/**
	 * Parse flag bits to produce enum instances.
	 * 
	 * @param <T> the enum type 
	 * @param clazz the enum class
	 * @param flags the bits to parse
	 * @return the enum instances
	 */
	public static <T extends Enum<T>> Collection<T> parse( Class<T> clazz, int flags ) {
	    FlagIO<T> parser = forClass( clazz ); 
	    return parser.parse( flags );
	}
	
	/**
	 * Generate the flag bits from a collection of enum instances
	 * 
	 * @param <T> the enum type
	 * @param enums the enum instances
	 * @return the flag bits
	 */
	public static <T extends Enum<T>> int toFlags( Class<T> clazz, Collection<T> enums ) {
        FlagIO<T> parser = forClass( clazz ); 
        return parser.toFlagBits( enums );
    }
	
	private FlagIO( Class<E> enumClass ) {
		this.enumClass = enumClass;
		enums2bits = new EnumMap<E, Integer>( enumClass );
		
		try {
			for( Field f : enumClass.getFields() ) {				
				if( enumClass.isAssignableFrom( f.getType() ) ) {
					Flag flag = f.getAnnotation( Flag.class );
					if( flag != null ) {
						
						@SuppressWarnings( "unchecked" )
						E e = (E) f.get(null);
						
						bits2enums.put( flag.value(), e );
						enums2bits.put( e, flag.value() );
					}
				}
			}
		} catch( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}
	
	/**
	 * Parse a set of enum constants from a set of flag bits.
	 * 
	 * @param flags the flag bits to parse
	 * @return set of enum constants
	 */
	private Set<E> parse( int flags ) {
		EnumSet<E> set = EnumSet.noneOf( enumClass );
				
		for( Map.Entry<Integer, E> entry : bits2enums.entrySet() ) {
			int bits = entry.getKey();
			E   e    = entry.getValue();
			
			if(( flags & bits ) != 0 ) set.add( e );
		}
		
		return set;
	}
	
	/**
	 * Regenerate the bit flags.
	 */
	private int toFlagBits( Collection<E> ee ) {
	    int flags = 0;
	    
	    for( E e : ee ) {
	        Integer bits = enums2bits.get( e );
	        if( bits != null ) flags |= bits;
	    }
	    
	    return flags;
	}
}
